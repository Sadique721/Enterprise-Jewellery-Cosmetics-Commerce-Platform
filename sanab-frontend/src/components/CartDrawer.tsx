'use client';

import React from 'react';
import Link from 'next/link';
import { X, Trash2, ShoppingBag, ArrowRight } from 'lucide-react';
import { useCartStore } from '@/store/useCartStore';

export const CartDrawer: React.FC = () => {
  const { items, isOpen, setIsOpen, removeItem, updateQuantity, getTotal } = useCartStore();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-hidden">
      <div className="absolute inset-0 bg-black/70 backdrop-blur-sm" onClick={() => setIsOpen(false)} />

      <div className="fixed inset-y-0 right-0 max-w-full flex pl-10">
        <div className="w-screen max-w-md bg-[#12121A] border-l border-white/10 text-gray-100 flex flex-col justify-between shadow-2xl">
          {/* Header */}
          <div className="p-6 border-b border-white/10 flex items-center justify-between">
            <div className="flex items-center gap-2">
              <ShoppingBag className="w-5 h-5 text-gold-400" />
              <h2 className="font-serif text-lg font-bold">Your Luxury Cart</h2>
            </div>
            <button onClick={() => setIsOpen(false)} className="text-gray-400 hover:text-white p-1">
              <X className="w-6 h-6" />
            </button>
          </div>

          {/* Items List */}
          <div className="p-6 flex-1 overflow-y-auto space-y-6">
            {items.length === 0 ? (
              <div className="text-center py-12 text-gray-400 space-y-3">
                <ShoppingBag className="w-12 h-12 mx-auto text-gray-600" />
                <p>Your shopping bag is empty.</p>
                <button
                  onClick={() => setIsOpen(false)}
                  className="text-gold-400 text-xs font-semibold hover:underline"
                >
                  BROWSE COLLECTIONS
                </button>
              </div>
            ) : (
              items.map((item) => (
                <div key={item.sku} className="flex gap-4 p-3 bg-white/5 rounded-lg border border-white/5">
                  <div className="w-16 h-16 bg-gray-900 rounded overflow-hidden flex-shrink-0">
                    <img src={item.imageUrl} alt={item.productName} className="w-full h-full object-cover" />
                  </div>
                  <div className="flex-1">
                    <h4 className="text-sm font-medium text-gray-200 line-clamp-1">{item.productName}</h4>
                    <p className="text-xs text-gold-400 mt-0.5">₹{item.unitPrice.toLocaleString('en-IN')}</p>
                    <div className="flex items-center justify-between mt-3">
                      <div className="flex items-center border border-white/10 rounded">
                        <button
                          onClick={() => updateQuantity(item.sku, item.quantity - 1)}
                          className="px-2 py-0.5 text-xs text-gray-400 hover:text-white"
                        >
                          -
                        </button>
                        <span className="px-2 text-xs font-medium text-white">{item.quantity}</span>
                        <button
                          onClick={() => updateQuantity(item.sku, item.quantity + 1)}
                          className="px-2 py-0.5 text-xs text-gray-400 hover:text-white"
                        >
                          +
                        </button>
                      </div>
                      <button onClick={() => removeItem(item.sku)} className="text-red-400 hover:text-red-300">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>

          {/* Footer Subtotal & Checkout */}
          {items.length > 0 && (
            <div className="p-6 border-t border-white/10 bg-[#0D0D11]/90 space-y-4">
              <div className="flex justify-between items-center text-sm">
                <span className="text-gray-400">Subtotal</span>
                <span className="text-lg font-bold font-serif text-gold-400">
                  ₹{getTotal().toLocaleString('en-IN')}
                </span>
              </div>
              <p className="text-[11px] text-gray-500">Taxes and insured shipping calculated at checkout.</p>
              <Link
                href="/checkout"
                onClick={() => setIsOpen(false)}
                className="w-full py-3.5 bg-gradient-to-r from-gold-500 to-gold-600 text-black font-bold text-sm tracking-wider uppercase rounded flex items-center justify-center gap-2 hover:from-gold-400 hover:to-gold-500 transition-all shadow-lg shadow-gold-500/20"
              >
                <span>PROCEED TO CHECKOUT</span>
                <ArrowRight className="w-4 h-4" />
              </Link>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
