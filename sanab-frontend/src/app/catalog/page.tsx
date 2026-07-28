'use client';

import React, { useState, useEffect, Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { ProductCard } from '@/components/ProductCard';
import { Search, Filter, SlidersHorizontal } from 'lucide-react';

const CATALOG_ITEMS = [
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
  {
    id: '5',
    name: 'Antique Peacock Gold Jhumka Earrings',
    slug: 'antique-peacock-jhumka',
    sku: 'JWL-EAR-005',
    categoryName: 'Bridal Jewellery',
    price: 92000,
    originalPrice: 105000,
    imageUrl: 'https://images.unsplash.com/photo-1630019852942-f89202989a59?q=80&w=800&auto=format&fit=crop',
    purityBadge: '22K Gold',
    rating: 5.0,
    reviewCount: 27,
  },
  {
    id: '6',
    name: 'Rose Gold Diamond Bracelet',
    slug: 'rose-gold-diamond-bracelet',
    sku: 'JWL-BRC-006',
    categoryName: 'Diamonds',
    price: 128000,
    originalPrice: 140000,
    imageUrl: 'https://images.unsplash.com/photo-1611591475281-8d282322b7f0?q=80&w=800&auto=format&fit=crop',
    purityBadge: 'VVS Certified',
    rating: 4.9,
    reviewCount: 61,
  },
];

function CatalogContent() {
  const searchParams = useSearchParams();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');

  useEffect(() => {
    const categoryParam = searchParams.get('category');
    if (categoryParam) {
      const upper = categoryParam.toUpperCase();
      if (upper.includes('JEWEL') || upper.includes('JEWELLERY')) {
        setSelectedCategory('JEWELLERY');
      } else if (upper.includes('DIAMOND') || upper.includes('SOLITAIRE')) {
        setSelectedCategory('DIAMONDS');
      } else if (upper.includes('COSMETIC') || upper.includes('SKINCARE')) {
        setSelectedCategory('COSMETICS');
      } else {
        setSelectedCategory('ALL');
      }
    }
  }, [searchParams]);

  const filteredItems = CATALOG_ITEMS.filter((item) => {
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          item.categoryName.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = selectedCategory === 'ALL' ||
                            (selectedCategory === 'JEWELLERY' && item.categoryName.includes('Jewellery')) ||
                            (selectedCategory === 'DIAMONDS' && (item.categoryName.includes('Diamonds') || item.categoryName.includes('Solitaire') || item.name.toLowerCase().includes('diamond'))) ||
                            (selectedCategory === 'COSMETICS' && (item.categoryName.includes('Cosmetics') || item.categoryName.includes('Skincare')));
    return matchesSearch && matchesCategory;
  });

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12 space-y-10">
      {/* Catalog Header */}
      <div className="space-y-4 text-center md:text-left">
        <span className="text-xs uppercase tracking-widest text-gold-400 font-bold">HAUTE CATALOGUE</span>
        <h1 className="text-3xl sm:text-4xl font-bold text-white font-serif">Luxury Collections</h1>
        <p className="text-sm text-gray-400 max-w-2xl font-light">
          Filter through our certified 22K gold heritage jewellery, solitaire diamonds, and dermatological cosmetics.
        </p>
      </div>

      {/* Filter Bar */}
      <div className="glass-card p-4 rounded-xl flex flex-col md:flex-row items-center justify-between gap-4">
        {/* Search input */}
        <div className="relative w-full md:w-80">
          <Search className="w-4 h-4 absolute left-3 top-3 text-gray-400" />
          <input
            type="text"
            placeholder="Search by name, SKU or gemstone..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full bg-white/5 border border-white/10 rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-gray-500 focus:outline-none focus:border-gold-500"
          />
        </div>

        {/* Category Pills */}
        <div className="flex items-center gap-2 overflow-x-auto w-full md:w-auto">
          <button
            onClick={() => setSelectedCategory('ALL')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold uppercase tracking-wider transition-colors ${
              selectedCategory === 'ALL'
                ? 'bg-gold-500 text-black'
                : 'bg-white/5 text-gray-300 hover:bg-white/10'
            }`}
          >
            All Products
          </button>
          <button
            onClick={() => setSelectedCategory('JEWELLERY')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold uppercase tracking-wider transition-colors ${
              selectedCategory === 'JEWELLERY'
                ? 'bg-gold-500 text-black'
                : 'bg-white/5 text-gray-300 hover:bg-white/10'
            }`}
          >
            Fine Jewellery
          </button>
          <button
            onClick={() => setSelectedCategory('DIAMONDS')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold uppercase tracking-wider transition-colors ${
              selectedCategory === 'DIAMONDS'
                ? 'bg-gold-500 text-black'
                : 'bg-white/5 text-gray-300 hover:bg-white/10'
            }`}
          >
            Solitaires & Diamonds
          </button>
          <button
            onClick={() => setSelectedCategory('COSMETICS')}
            className={`px-4 py-2 rounded-lg text-xs font-semibold uppercase tracking-wider transition-colors ${
              selectedCategory === 'COSMETICS'
                ? 'bg-gold-500 text-black'
                : 'bg-white/5 text-gray-300 hover:bg-white/10'
            }`}
          >
            Cosmetics
          </button>
        </div>
      </div>

      {/* Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
        {filteredItems.map((item) => (
          <ProductCard key={item.id} product={item} />
        ))}
      </div>
    </div>
  );
}

export default function CatalogPage() {
  return (
    <Suspense fallback={<div className="text-center py-12 text-gold-400">Loading Catalogue...</div>}>
      <CatalogContent />
    </Suspense>
  );
}
