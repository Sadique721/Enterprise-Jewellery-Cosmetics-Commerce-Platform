'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { ShoppingBag, Search, User, Menu, X, Sparkles, Heart } from 'lucide-react';
import { useCartStore } from '@/store/useCartStore';

export const Header: React.FC = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const { items, setIsOpen } = useCartStore();
  const itemCount = items.reduce((acc, i) => acc + i.quantity, 0);

  return (
    <header className="sticky top-0 z-50 bg-[#0D0D11]/90 backdrop-blur-md border-b border-white/10">
      {/* Top Banner */}
      <div className="bg-gradient-to-r from-gold-900 via-gold-600 to-gold-900 text-xs py-1.5 px-4 text-center font-medium tracking-widest text-gold-100 flex items-center justify-center gap-2">
        <Sparkles className="w-3.5 h-3.5" />
        <span>COMPLIMENTARY INSURED SHIPPING ON ALL LUXURY ORDERS ABOVE ₹25,000</span>
        <Sparkles className="w-3.5 h-3.5" />
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-20">
          {/* Mobile menu button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden text-gray-300 hover:text-white p-2"
          >
            {mobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
          </button>

          {/* Logo */}
          <Link href="/" className="flex items-center gap-2">
            <span className="font-serif text-3xl font-bold tracking-widest gold-gradient-text">
              SANAB
            </span>
            <span className="text-[10px] tracking-widest uppercase text-gold-400 font-sans border-l border-gold-500/30 pl-2 hidden sm:inline-block">
              Haute Joaillerie & Cosmétiques
            </span>
          </Link>

          {/* Desktop Nav */}
          <nav className="hidden md:flex items-center space-x-8 text-sm font-medium tracking-wide">
            <Link href="/catalog?category=JEWELLERY" className="text-gray-300 hover:text-gold-400 transition-colors">
              Fine Jewellery
            </Link>
            <Link href="/catalog?category=DIAMONDS" className="text-gray-300 hover:text-gold-400 transition-colors">
              Solitaires & Diamonds
            </Link>
            <Link href="/catalog?category=COSMETICS" className="text-gray-300 hover:text-gold-400 transition-colors">
              Luxury Cosmetics
            </Link>
            <Link href="/catalog?category=COLLECTIONS" className="text-gray-300 hover:text-gold-400 transition-colors">
              Royal Heritage
            </Link>
          </nav>

          {/* Right Action Icons */}
          <div className="flex items-center space-x-5">
            <Link href="/catalog" className="text-gray-300 hover:text-gold-400 p-2 transition-colors">
              <Search className="w-5 h-5" />
            </Link>

            <Link href="/account" className="text-gray-300 hover:text-gold-400 p-2 transition-colors">
              <User className="w-5 h-5" />
            </Link>

            <button
              onClick={() => setIsOpen(true)}
              className="relative p-2 text-gray-300 hover:text-gold-400 transition-colors"
            >
              <ShoppingBag className="w-5 h-5" />
              {itemCount > 0 && (
                <span className="absolute -top-1 -right-1 bg-gold-500 text-black font-bold text-[10px] w-4 h-4 rounded-full flex items-center justify-center">
                  {itemCount}
                </span>
              )}
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Drawer Nav */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-[#12121A] border-b border-white/10 px-4 pt-2 pb-6 space-y-4">
          <Link href="/catalog?category=JEWELLERY" className="block text-gray-200 hover:text-gold-400 py-2">
            Fine Jewellery
          </Link>
          <Link href="/catalog?category=DIAMONDS" className="block text-gray-200 hover:text-gold-400 py-2">
            Solitaires & Diamonds
          </Link>
          <Link href="/catalog?category=COSMETICS" className="block text-gray-200 hover:text-gold-400 py-2">
            Luxury Cosmetics
          </Link>
          <Link href="/admin" className="block text-gold-400 font-semibold py-2">
            Admin Console
          </Link>
        </div>
      )}
    </header>
  );
};
