'use client';

import React, { useState } from 'react';
import { useCartStore } from '@/store/useCartStore';
import { ShieldCheck, Lock, CreditCard, CheckCircle2, Truck } from 'lucide-react';
import Link from 'next/link';

export default function CheckoutPage() {
  const { items, getTotal, clearCart } = useCartStore();
  const [paymentMethod, setPaymentMethod] = useState<'AUTHORIZE_NET' | 'CASH_ON_DELIVERY'>('AUTHORIZE_NET');
  const [couponCode, setCouponCode] = useState('');
  const [discount, setDiscount] = useState(0);
  const [isOrdered, setIsOrdered] = useState(false);
  const [orderNumber, setOrderNumber] = useState('');

  const handleApplyCoupon = (e: React.FormEvent) => {
    e.preventDefault();
    if (couponCode.trim().toUpperCase() === 'ROYAL10') {
      const discountVal = getTotal() * 0.1;
      setDiscount(discountVal);
      alert('Coupon ROYAL10 applied! 10% discount applied.');
    } else {
      alert('Invalid coupon code. Try ROYAL10');
    }
  };

  const handlePlaceOrder = (e: React.FormEvent) => {
    e.preventDefault();
    const orderNo = 'SANAB-' + Math.floor(100000 + Math.random() * 900000);
    setOrderNumber(orderNo);
    setIsOrdered(true);
    clearCart();
  };

  if (isOrdered) {
    return (
      <div className="max-w-3xl mx-auto px-4 py-20 text-center space-y-6">
        <div className="w-16 h-16 bg-gold-500/20 text-gold-400 rounded-full flex items-center justify-center mx-auto border border-gold-500/40">
          <CheckCircle2 className="w-10 h-10" />
        </div>
        <h1 className="text-3xl font-bold font-serif text-white">Order Confirmed!</h1>
        <p className="text-sm text-gray-300">
          Thank you for choosing SANAB. Your order <span className="text-gold-400 font-bold">{orderNumber}</span> has been dispatched for insured packaging.
        </p>
        <div className="glass-card p-6 rounded-xl text-left max-w-md mx-auto space-y-3 text-xs text-gray-300">
          <div className="flex justify-between border-b border-white/10 pb-2">
            <span>Estimated Delivery</span>
            <span className="font-semibold text-white">3-5 Business Days</span>
          </div>
          <div className="flex justify-between border-b border-white/10 pb-2">
            <span>Carrier</span>
            <span className="font-semibold text-white">Bluedart Insured Express</span>
          </div>
          <div className="flex justify-between">
            <span>Notification</span>
            <span className="font-semibold text-gold-400">SMS & Email Sent</span>
          </div>
        </div>
        <Link
          href="/catalog"
          className="inline-block px-8 py-3 bg-gold-500 text-black font-bold text-xs uppercase tracking-widest rounded hover:bg-gold-400"
        >
          CONTINUE SHOPPING
        </Link>
      </div>
    );
  }

  const grandTotal = Math.max(0, getTotal() - discount);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-10">
      <div className="space-y-2">
        <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">SECURE CHECKOUT</span>
        <h1 className="text-3xl font-bold text-white font-serif">Complete Your Purchase</h1>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-12">
        {/* Form Left */}
        <form onSubmit={handlePlaceOrder} className="lg:col-span-2 space-y-8">
          {/* Shipping Address */}
          <div className="glass-card p-6 rounded-xl space-y-4">
            <h2 className="text-base font-semibold text-white flex items-center gap-2 border-b border-white/10 pb-3">
              <Truck className="w-5 h-5 text-gold-400" />
              <span>1. Delivery Address</span>
            </h2>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
              <div>
                <label className="block text-gray-400 mb-1">Full Name</label>
                <input required type="text" defaultValue="Maharani Priya Sharma" className="w-full bg-white/5 border border-white/10 rounded px-3 py-2 text-white focus:outline-none focus:border-gold-500" />
              </div>
              <div>
                <label className="block text-gray-400 mb-1">Phone Number</label>
                <input required type="text" defaultValue="+91 98765 43210" className="w-full bg-white/5 border border-white/10 rounded px-3 py-2 text-white focus:outline-none focus:border-gold-500" />
              </div>
              <div className="sm:col-span-2">
                <label className="block text-gray-400 mb-1">Street Address</label>
                <input required type="text" defaultValue="Flat 402, Royal Palace Apartments, MG Road" className="w-full bg-white/5 border border-white/10 rounded px-3 py-2 text-white focus:outline-none focus:border-gold-500" />
              </div>
              <div>
                <label className="block text-gray-400 mb-1">City</label>
                <input required type="text" defaultValue="Mumbai" className="w-full bg-white/5 border border-white/10 rounded px-3 py-2 text-white focus:outline-none focus:border-gold-500" />
              </div>
              <div>
                <label className="block text-gray-400 mb-1">Postal Code</label>
                <input required type="text" defaultValue="400001" className="w-full bg-white/5 border border-white/10 rounded px-3 py-2 text-white focus:outline-none focus:border-gold-500" />
              </div>
            </div>
          </div>

          {/* Payment Method */}
          <div className="glass-card p-6 rounded-xl space-y-4">
            <h2 className="text-base font-semibold text-white flex items-center gap-2 border-b border-white/10 pb-3">
              <CreditCard className="w-5 h-5 text-gold-400" />
              <span>2. Payment Gateway</span>
            </h2>

            <div className="space-y-3">
              <label
                onClick={() => setPaymentMethod('AUTHORIZE_NET')}
                className={`flex items-center justify-between p-4 rounded-lg border cursor-pointer transition-colors ${
                  paymentMethod === 'AUTHORIZE_NET'
                    ? 'border-gold-500 bg-gold-500/10'
                    : 'border-white/10 bg-white/5'
                }`}
              >
                <div className="flex items-center gap-3">
                  <input type="radio" checked={paymentMethod === 'AUTHORIZE_NET'} onChange={() => {}} className="accent-gold-500" />
                  <div>
                    <h4 className="text-sm font-semibold text-white">Credit / Debit Card (Authorize.Net)</h4>
                    <p className="text-xs text-gray-400">256-bit SSL encrypted secure payment</p>
                  </div>
                </div>
                <Lock className="w-4 h-4 text-gold-400" />
              </label>

              <label
                onClick={() => setPaymentMethod('CASH_ON_DELIVERY')}
                className={`flex items-center justify-between p-4 rounded-lg border cursor-pointer transition-colors ${
                  paymentMethod === 'CASH_ON_DELIVERY'
                    ? 'border-gold-500 bg-gold-500/10'
                    : 'border-white/10 bg-white/5'
                }`}
              >
                <div className="flex items-center gap-3">
                  <input type="radio" checked={paymentMethod === 'CASH_ON_DELIVERY'} onChange={() => {}} className="accent-gold-500" />
                  <div>
                    <h4 className="text-sm font-semibold text-white">Cash on Delivery (COD)</h4>
                    <p className="text-xs text-gray-400">Insured physical handover verification</p>
                  </div>
                </div>
                <ShieldCheck className="w-4 h-4 text-gold-400" />
              </label>
            </div>
          </div>

          <button
            type="submit"
            className="w-full py-4 bg-gradient-to-r from-gold-500 to-gold-600 text-black font-bold text-sm tracking-widest uppercase rounded hover:from-gold-400 hover:to-gold-500 transition-all shadow-lg shadow-gold-500/20"
          >
            CONFIRM & PLACE ORDER (₹{grandTotal.toLocaleString('en-IN')})
          </button>
        </form>

        {/* Order Summary Right */}
        <div className="glass-card p-6 rounded-xl h-fit space-y-6">
          <h2 className="text-base font-semibold text-white border-b border-white/10 pb-3">Order Summary</h2>

          {/* Coupon */}
          <form onSubmit={handleApplyCoupon} className="flex gap-2">
            <input
              type="text"
              placeholder="Coupon Code (e.g. ROYAL10)"
              value={couponCode}
              onChange={(e) => setCouponCode(e.target.value)}
              className="bg-white/5 border border-white/10 rounded px-3 py-2 text-xs text-white uppercase placeholder-gray-500 focus:outline-none focus:border-gold-500 w-full"
            />
            <button type="submit" className="bg-gold-500 text-black font-bold text-xs px-4 py-2 rounded hover:bg-gold-400">
              APPLY
            </button>
          </form>

          {/* Breakdown */}
          <div className="space-y-3 text-xs text-gray-300 border-t border-white/10 pt-4">
            <div className="flex justify-between">
              <span>Subtotal</span>
              <span className="text-white font-medium">₹{getTotal().toLocaleString('en-IN')}</span>
            </div>
            {discount > 0 && (
              <div className="flex justify-between text-gold-400">
                <span>Promotional Discount</span>
                <span>-₹{discount.toLocaleString('en-IN')}</span>
              </div>
            )}
            <div className="flex justify-between">
              <span>Insured Shipping</span>
              <span className="text-emerald-400 font-semibold">FREE</span>
            </div>
            <div className="flex justify-between text-base font-bold text-white border-t border-white/10 pt-3 font-serif">
              <span>Grand Total</span>
              <span className="text-gold-400">₹{grandTotal.toLocaleString('en-IN')}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
