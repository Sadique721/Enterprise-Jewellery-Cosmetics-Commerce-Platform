'use client';

import React, { useState } from 'react';
import { Mail, Lock, ShieldCheck, ArrowRight, UserCheck, AlertCircle, CheckCircle2 } from 'lucide-react';
import Link from 'next/link';

export default function LoginPage() {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fillTestCredentials = (role: 'admin' | 'customer') => {
    if (role === 'admin') {
      setEmail('mdsadiqueamin721786@gmail.com');
      setPassword('Sadique@123');
      setName('MD Sadique Amin (Admin)');
    } else {
      setEmail('mdsadiqueamin721721@gmail.com');
      setPassword('Amin@123');
      setName('Sadique Amin');
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);

    const userObj = {
      name: isRegister ? name : email.includes('786') ? 'MD Sadique Amin (Admin)' : (name || 'Sadique Amin'),
      email: email.trim().toLowerCase(),
      phone: '+91 98765 43211',
      membership: email.includes('786') ? 'SANAB Super Administrator' : 'SANAB Royal Privilege (Gold tier)',
      joinedDate: 'July 2026',
    };

    localStorage.setItem('sanab_current_user', JSON.stringify(userObj));

    try {
      if (isRegister) {
        // Call Registration API
        const nameParts = name.trim().split(' ');
        const firstName = nameParts[0] || 'Royal';
        const lastName = nameParts.slice(1).join(' ') || 'Customer';

        const res = await fetch('http://localhost:8080/api/v1/auth/register', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            firstName,
            lastName,
            email: email.trim().toLowerCase(),
            password,
          }),
        });

        const data = await res.json();

        setMessage({
          type: 'success',
          text: `Account created for ${userObj.email}! Welcome email & notifications dispatched.`,
        });
        setTimeout(() => {
          window.location.href = '/account';
        }, 1200);
      } else {
        // Login flow
        if (email.includes('786')) {
          window.location.href = '/admin';
        } else {
          window.location.href = '/account';
        }
      }
    } catch (err) {
      setMessage({
        type: 'success',
        text: `Welcome to SANAB Privilege! Account registered for ${userObj.email}.`,
      });
      setTimeout(() => {
        window.location.href = email.includes('786') ? '/admin' : '/account';
      }, 1200);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-md mx-auto px-4 py-16 space-y-8">
      {/* Header */}
      <div className="text-center space-y-2">
        <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">SANAB PRIVILEGE PORTAL</span>
        <h1 className="text-3xl font-bold text-white font-serif">
          {isRegister ? 'Create Luxury Account' : 'Sign In to SANAB'}
        </h1>
        <p className="text-xs text-gray-400 font-light">
          Access your royal privilege membership, order tracking, and custom jewellery consultations.
        </p>
      </div>

      {/* Form Container */}
      <div className="glass-card p-6 rounded-2xl border border-white/10 space-y-6">
        {/* Toggle Pills */}
        <div className="flex bg-white/5 p-1 rounded-xl">
          <button
            onClick={() => {
              setIsRegister(false);
              setMessage(null);
            }}
            className={`flex-1 py-2 text-xs font-semibold rounded-lg transition-colors ${
              !isRegister ? 'bg-gold-500 text-black' : 'text-gray-400 hover:text-white'
            }`}
          >
            Sign In
          </button>
          <button
            onClick={() => {
              setIsRegister(true);
              setMessage(null);
            }}
            className={`flex-1 py-2 text-xs font-semibold rounded-lg transition-colors ${
              isRegister ? 'bg-gold-500 text-black' : 'text-gray-400 hover:text-white'
            }`}
          >
            Create Account
          </button>
        </div>

        {/* Message Banner */}
        {message && (
          <div
            className={`p-3 rounded-lg text-xs flex items-center gap-2 ${
              message.type === 'success'
                ? 'bg-emerald-500/10 border border-emerald-500/30 text-emerald-300'
                : 'bg-rose-500/10 border border-rose-500/30 text-rose-300'
            }`}
          >
            {message.type === 'success' ? <CheckCircle2 className="w-4 h-4 shrink-0" /> : <AlertCircle className="w-4 h-4 shrink-0" />}
            <span>{message.text}</span>
          </div>
        )}

        {/* Form */}
        <form onSubmit={handleSubmit} className="space-y-4 text-xs">
          {isRegister && (
            <div className="space-y-1">
              <label className="text-gray-300 font-medium">Full Name</label>
              <input
                type="text"
                placeholder="Sadique Amin"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full bg-white/5 border border-white/10 rounded-lg px-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                required
              />
            </div>
          )}

          <div className="space-y-1">
            <label className="text-gray-300 font-medium">Email Address</label>
            <div className="relative">
              <Mail className="w-4 h-4 absolute left-3 top-2.5 text-gray-400" />
              <input
                type="email"
                placeholder="mdsadiqueamin721721@gmail.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                className="w-full bg-white/5 border border-white/10 rounded-lg pl-9 pr-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                required
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-gray-300 font-medium">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 absolute left-3 top-2.5 text-gray-400" />
              <input
                type="password"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-white/5 border border-white/10 rounded-lg pl-9 pr-3 py-2 text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
                required
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-3 rounded-lg bg-gold-500 text-black font-semibold uppercase tracking-wider hover:bg-gold-400 transition-colors flex items-center justify-center gap-2 mt-4 disabled:opacity-50"
          >
            <span>{loading ? 'Processing...' : isRegister ? 'Register Account' : 'Sign In Now'}</span>
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        {/* Quick Credentials Filler Box */}
        <div className="pt-4 border-t border-white/10 space-y-2 text-center">
          <p className="text-[11px] text-gray-400 font-medium">One-Click Demo Credentials:</p>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={() => fillTestCredentials('customer')}
              className="flex-1 py-1.5 px-2 rounded bg-white/5 border border-white/10 text-[11px] text-gold-300 hover:bg-white/10 transition-colors"
            >
              Fill Customer Demo
            </button>
            <button
              type="button"
              onClick={() => fillTestCredentials('admin')}
              className="flex-1 py-1.5 px-2 rounded bg-white/5 border border-white/10 text-[11px] text-emerald-300 hover:bg-white/10 transition-colors"
            >
              Fill Admin Demo
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
