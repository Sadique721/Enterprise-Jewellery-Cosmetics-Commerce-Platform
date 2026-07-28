'use client';

import React from 'react';
import Link from 'next/link';
import { ProductCard } from '@/components/ProductCard';
import { Sparkles, ArrowRight, Gem, Crown, Sparkle, ShieldCheck } from 'lucide-react';

const FEATURED_PRODUCTS = [
  {
    id: '1',
    name: 'Royal Heritage Kundan Choker Necklace',
    slug: 'royal-heritage-kundan-choker',
    sku: 'JWL-KND-001',
    categoryName: 'Bridal Jewellery',
    price: 185000,
    originalPrice: 210000,
    imageUrl: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?q=80&w=800&auto=format&fit=crop',
    purityBadge: '22K Hallmarked Gold',
    rating: 5.0,
    reviewCount: 32,
  },
  {
    id: '2',
    name: 'Solitaire Diamond Engagement Ring',
    slug: 'solitaire-diamond-ring',
    sku: 'JWL-DMD-002',
    categoryName: 'Solitaire Rings',
    price: 145000,
    originalPrice: 160000,
    imageUrl: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?q=80&w=800&auto=format&fit=crop',
    purityBadge: 'VVS1 IGI Certified',
    rating: 4.9,
    reviewCount: 48,
  },
  {
    id: '3',
    name: '24K Gold Infused Regenerative Serum',
    slug: '24k-gold-regenerative-serum',
    sku: 'CSM-SRM-001',
    categoryName: 'Luxury Skincare',
    price: 8500,
    originalPrice: 10500,
    imageUrl: 'https://images.unsplash.com/photo-1620916566398-39f1143ab7be?q=80&w=800&auto=format&fit=crop',
    purityBadge: 'Clinical Grade',
    rating: 4.8,
    reviewCount: 94,
  },
  {
    id: '4',
    name: 'Velvet Matte Royal Ruby Lipstick',
    slug: 'velvet-matte-royal-ruby',
    sku: 'CSM-LPS-002',
    categoryName: 'Cosmetics',
    price: 3200,
    originalPrice: 3800,
    imageUrl: 'https://images.unsplash.com/photo-1586495777744-4413f21062fa?q=80&w=800&auto=format&fit=crop',
    purityBadge: 'Organic Pigment',
    rating: 4.9,
    reviewCount: 112,
  },
];

export default function HomePage() {
  return (
    <div className="space-y-24 pb-16">
      {/* Hero Section */}
      <section className="relative h-[85vh] flex items-center justify-center overflow-hidden">
        <div className="absolute inset-0 bg-black/60 z-10" />
        <img
          src="https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?q=80&w=1920&auto=format&fit=crop"
          alt="SANAB Royal Collection"
          className="absolute inset-0 w-full h-full object-cover scale-105 animate-pulse duration-10000"
        />
        <div className="relative z-20 text-center max-w-4xl px-4 space-y-6">
          <span className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full border border-gold-500/40 bg-black/50 text-gold-300 text-xs font-semibold tracking-widest uppercase backdrop-blur-md">
            <Crown className="w-4 h-4" />
            ROYAL HERITAGE COLLECTION 2026
          </span>
          <h1 className="text-4xl sm:text-6xl font-bold tracking-tight gold-gradient-text leading-tight">
            Timeless Elegance.<br />Uncompromising Luxury.
          </h1>
          <p className="text-gray-300 text-sm sm:text-base max-w-2xl mx-auto font-light leading-relaxed">
            Discover handcrafted 22K gold bridal jewellery and dermatologically formulated 24K gold skin elixirs created for royalty.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-4 pt-4">
            <Link
              href="/catalog"
              className="w-full sm:w-auto px-8 py-3.5 bg-gradient-to-r from-gold-500 to-gold-600 text-black font-bold text-xs uppercase tracking-widest rounded hover:from-gold-400 hover:to-gold-500 transition-all shadow-lg shadow-gold-500/20"
            >
              EXPLORE JEWELLERY
            </Link>
            <Link
              href="/catalog?category=COSMETICS"
              className="w-full sm:w-auto px-8 py-3.5 border border-white/20 text-white font-bold text-xs uppercase tracking-widest rounded hover:bg-white/10 transition-colors backdrop-blur-sm"
            >
              DISCOVER COSMETICS
            </Link>
          </div>
        </div>
      </section>

      {/* Categories Showcase */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        <div className="text-center space-y-2">
          <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">CURATED CATEGORIES</span>
          <h2 className="text-3xl font-bold text-white">Crafted For Royalty</h2>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <Link href="/catalog?category=JEWELLERY" className="group relative h-96 rounded-2xl overflow-hidden glass-card">
            <img
              src="https://images.unsplash.com/photo-1611591475281-8d282322b7f0?q=80&w=800&auto=format&fit=crop"
              alt="Bridal Jewellery"
              className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/30 to-transparent p-8 flex flex-col justify-end">
              <span className="text-xs font-bold text-gold-400 uppercase tracking-widest">FINE JEWELLERY</span>
              <h3 className="text-2xl font-bold text-white font-serif mt-1">Bridal & Heritage</h3>
              <p className="text-xs text-gray-300 mt-2">22K Hallmarked Gold, Uncut Kundan & Polki sets.</p>
            </div>
          </Link>

          <Link href="/catalog?category=DIAMONDS" className="group relative h-96 rounded-2xl overflow-hidden glass-card">
            <img
              src="https://images.unsplash.com/photo-1605100804763-247f67b3557e?q=80&w=800&auto=format&fit=crop"
              alt="Solitaires"
              className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/30 to-transparent p-8 flex flex-col justify-end">
              <span className="text-xs font-bold text-gold-400 uppercase tracking-widest">SOLITAIRES</span>
              <h3 className="text-2xl font-bold text-white font-serif mt-1">IGI Certified Diamonds</h3>
              <p className="text-xs text-gray-300 mt-2">VVS1 D-Color Solitaires and engagement bands.</p>
            </div>
          </Link>

          <Link href="/catalog?category=COSMETICS" className="group relative h-96 rounded-2xl overflow-hidden glass-card">
            <img
              src="https://images.unsplash.com/photo-1620916566398-39f1143ab7be?q=80&w=800&auto=format&fit=crop"
              alt="Luxury Cosmetics"
              className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/90 via-black/30 to-transparent p-8 flex flex-col justify-end">
              <span className="text-xs font-bold text-gold-400 uppercase tracking-widest">LUXURY COSMETICS</span>
              <h3 className="text-2xl font-bold text-white font-serif mt-1">24K Gold Skin Elixirs</h3>
              <p className="text-xs text-gray-300 mt-2">Dermatologically active anti-aging formulations.</p>
            </div>
          </Link>
        </div>
      </section>

      {/* Featured Products */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 space-y-10">
        <div className="flex flex-col sm:flex-row items-start sm:items-end justify-between gap-4">
          <div>
            <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">BESTSELLERS</span>
            <h2 className="text-3xl font-bold text-white mt-1">Iconic Creations</h2>
          </div>
          <Link href="/catalog" className="text-gold-400 text-xs font-bold tracking-widest hover:underline flex items-center gap-2">
            <span>VIEW ALL CREATIONS</span>
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {FEATURED_PRODUCTS.map((prod) => (
            <ProductCard key={prod.id} product={prod} />
          ))}
        </div>
      </section>

      {/* Brand Story Banner */}
      <section className="relative bg-[#12121A] py-20 border-y border-white/10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div className="space-y-6">
            <span className="text-xs font-bold text-gold-400 uppercase tracking-widest">THE SANAB PROMISE</span>
            <h2 className="text-3xl sm:text-4xl font-bold text-white leading-snug">
              Every Piece Tells a Royal Story.
            </h2>
            <p className="text-gray-300 text-sm leading-relaxed font-light">
              Crafted by master goldsmiths whose families served royal courts for centuries, SANAB combines ancient Kundan, Meenakari, and Polki artistry with contemporary Swiss diamond settings.
            </p>
            <div className="grid grid-cols-2 gap-6 pt-4 text-xs">
              <div className="space-y-1">
                <span className="text-2xl font-serif font-bold text-gold-400">100%</span>
                <p className="text-gray-400">Hallmarked 22K Gold & IGI Diamonds</p>
              </div>
              <div className="space-y-1">
                <span className="text-2xl font-serif font-bold text-gold-400">Lifetime</span>
                <p className="text-gray-400">Transparent Exchange & Buyback Value</p>
              </div>
            </div>
          </div>
          <div className="relative aspect-4/3 rounded-2xl overflow-hidden border border-white/10 shadow-2xl">
            <img
              src="https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?q=80&w=1000&auto=format&fit=crop"
              alt="Craftsmanship"
              className="w-full h-full object-cover"
            />
          </div>
        </div>
      </section>
    </div>
  );
}
