'use client';

import React from 'react';
import Link from 'next/link';
import { ShieldCheck, Award, Truck, RefreshCw } from 'lucide-react';

export const Footer: React.FC = () => {
  return (
    <footer className="bg-[#09090D] border-t border-white/10 text-gray-400 text-sm mt-20">
      {/* Trust Badges */}
      <div className="border-b border-white/10 py-10">
        <div className="max-w-7xl mx-auto px-4 grid grid-cols-2 md:grid-cols-4 gap-8 text-center">
          <div className="flex flex-col items-center">
            <ShieldCheck className="w-8 h-8 text-gold-400 mb-2" />
            <h4 className="text-gray-200 font-medium">100% Certified Hallmarked Gold</h4>
            <p className="text-xs text-gray-500 mt-1">BIS 916 & IGI Certified Diamonds</p>
          </div>
          <div className="flex flex-col items-center">
            <Truck className="w-8 h-8 text-gold-400 mb-2" />
            <h4 className="text-gray-200 font-medium">Insured Express Shipping</h4>
            <p className="text-xs text-gray-500 mt-1">Tamper-evident luxury packaging</p>
          </div>
          <div className="flex flex-col items-center">
            <RefreshCw className="w-8 h-8 text-gold-400 mb-2" />
            <h4 className="text-gray-200 font-medium">15-Day Easy Returns</h4>
            <p className="text-xs text-gray-500 mt-1">Hassle-free exchange policy</p>
          </div>
          <div className="flex flex-col items-center">
            <Award className="w-8 h-8 text-gold-400 mb-2" />
            <h4 className="text-gray-200 font-medium">Lifetime Buyback Guarantee</h4>
            <p className="text-xs text-gray-500 mt-1">Transparent benchmark valuation</p>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-16 grid grid-cols-1 md:grid-cols-4 gap-12">
        <div className="space-y-4">
          <h3 className="font-serif text-2xl font-bold gold-gradient-text">SANAB</h3>
          <p className="text-xs leading-relaxed text-gray-400">
            SANAB represents the pinnacle of handcrafted Indian bridal jewellery and dermatologically proven luxury cosmetics, curated for royalty across the world.
          </p>
        </div>

        <div>
          <h4 className="text-white font-medium mb-4 tracking-wider">FINE JEWELLERY</h4>
          <ul className="space-y-2 text-xs">
            <li><Link href="/catalog?category=NECKLACE" className="hover:text-gold-400">Bridal Necklaces</Link></li>
            <li><Link href="/catalog?category=RINGS" className="hover:text-gold-400">Diamond Solitaires</Link></li>
            <li><Link href="/catalog?category=EARRINGS" className="hover:text-gold-400">Kundan & Polki Earrings</Link></li>
            <li><Link href="/catalog?category=BANGLES" className="hover:text-gold-400">Gold Bangles & Kadas</Link></li>
          </ul>
        </div>

        <div>
          <h4 className="text-white font-medium mb-4 tracking-wider">CUSTOMER CARE</h4>
          <ul className="space-y-2 text-xs">
            <li><Link href="/support" className="hover:text-gold-400">Track Order Status</Link></li>
            <li><Link href="/support" className="hover:text-gold-400">Schedule Virtual Appointment</Link></li>
            <li><Link href="/support" className="hover:text-gold-400">Jewellery Care Guide</Link></li>
            <li><Link href="/admin" className="text-gold-400 hover:underline">Admin Executive Console</Link></li>
          </ul>
        </div>

        <div>
          <h4 className="text-white font-medium mb-4 tracking-wider">PRIVÉ NEWSLETTER</h4>
          <p className="text-xs mb-4 text-gray-400">Receive private previews of handcrafted bridal collections and bespoke cosmetic releases.</p>
          <div className="flex">
            <input
              type="email"
              placeholder="Enter your email"
              className="bg-white/5 border border-white/10 px-3 py-2 text-xs text-white placeholder-gray-500 rounded-l focus:outline-none focus:border-gold-500 w-full"
            />
            <button className="bg-gold-500 text-black font-semibold text-xs px-4 py-2 rounded-r hover:bg-gold-400 transition-colors">
              JOIN
            </button>
          </div>
        </div>
      </div>

      <div className="border-t border-white/5 py-6 text-center text-xs text-gray-500">
        © 2026 SANAB Antigravity Technology Platform. All Rights Reserved. Enterprise Edition.
      </div>
    </footer>
  );
};
