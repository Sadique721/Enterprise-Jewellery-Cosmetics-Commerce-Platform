package com.antigravity.sanab.identity.api.controller;

import com.antigravity.sanab.identity.api.dto.request.LoginRequest;
import com.antigravity.sanab.identity.api.dto.request.RegisterRequest;
import com.antigravity.sanab.identity.api.dto.response.AuthResponse;
import com.antigravity.sanab.identity.application.service.AuthService;
import com.antigravity.sanab.shared.api.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Authentication REST controller — handles all auth lifecycle endpoints.
 *
 * <p>All endpoints follow a consistent response structure using {@link ApiResponse}.
 * Error handling is centralized in {@link com.antigravity.sanab.shared.exception.GlobalExceptionHandler}.
 *
 * @author Antigravity Engineering
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User registration, login, token refresh, and account verification")
public class AuthController {

    private final AuthService authService;

    // ─── Registration ─────────────────────────────────────────────────────────

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest) {

        AuthResponse response = authService.register(request, getClientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Registration successful. Please verify your email."));
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    @Operation(summary = "Authenticate with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        AuthResponse response = authService.login(
                request,
                getClientIp(httpRequest),
                httpRequest.getHeader("User-Agent"));

        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    // ─── Token Refresh ────────────────────────────────────────────────────────

    @PostMapping("/refresh")
    @Operation(summary = "Rotate access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(
            @RequestBody Map<String, String> body,
            HttpServletRequest httpRequest) {

        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Refresh token is required"));
        }

        AuthResponse response = authService.refreshTokens(refreshToken, getClientIp(httpRequest));
        return ResponseEntity.ok(ApiResponse.success(response, "Tokens refreshed"));
    }

    // ─── Logout ───────────────────────────────────────────────────────────────

    @PostMapping("/logout")
    @Operation(summary = "Logout current session")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String authHeader) {

        String accessToken  = authHeader.substring(7); // Remove "Bearer "
        String refreshToken = body.get("refreshToken");
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout all active sessions")
    public ResponseEntity<ApiResponse<Void>> logoutAll(
            @AuthenticationPrincipal String userIdStr) {

        authService.logoutAll(UUID.fromString(userIdStr));
        return ResponseEntity.ok(ApiResponse.success(null, "All sessions terminated"));
    }

    // ─── Email Verification ───────────────────────────────────────────────────

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email address with OTP")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @RequestBody Map<String, String> body) {

        String userId = body.get("userId");
        String otp    = body.get("otp");
        authService.verifyEmail(UUID.fromString(userId), otp);
        return ResponseEntity.ok(ApiResponse.success(null, "Email verified successfully"));
    }

    // ─── Password Reset ───────────────────────────────────────────────────────

    @PostMapping("/forgot-password")
    @Operation(summary = "Initiate password reset — sends OTP to registered email")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(
            @RequestBody Map<String, String> body) {

        authService.initiatePasswordReset(body.get("email"));
        // Always return 200 to prevent email enumeration
        return ResponseEntity.ok(ApiResponse.success(null,
                "If an account exists with this email, a reset OTP has been sent."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Complete password reset with OTP")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @RequestBody Map<String, String> body) {

        authService.resetPassword(
                body.get("email"),
                body.get("otp"),
                body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success(null, "Password reset successfully"));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password for authenticated user")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal String userIdStr,
            @RequestBody Map<String, String> body) {

        authService.changePassword(
                UUID.fromString(userIdStr),
                body.get("currentPassword"),
                body.get("newPassword"));
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    // ─── Private Helpers ──────────────────────────────────────────────────────

    private String getClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
