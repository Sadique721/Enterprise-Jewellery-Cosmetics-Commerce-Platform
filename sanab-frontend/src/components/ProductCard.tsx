'use client';

import React from 'react';
import Link from 'next/link';
import { Star, ShoppingBag, Heart } from 'lucide-react';
import { useCartStore } from '@/store/useCartStore';
import { useWishlistStore } from '@/store/useWishlistStore';

export interface ProductProps {
  id: string;
  name: string;
  slug: string;
  sku: string;
  categoryName: string;
  price: number;
  originalPrice?: number;
  imageUrl: string;
  rating?: number;
  reviewCount?: number;
  purityBadge?: string;
}

export const ProductCard: React.FC<{ product: ProductProps }> = ({ product }) => {
  const addItem = useCartStore((state) => state.addItem);
  const { toggleWishlist, isInWishlist } = useWishlistStore();
  const isWishlisted = isInWishlist(product.id);

  return (
    <div className="group glass-card rounded-xl overflow-hidden transition-all duration-300 hover:border-gold-500/40 hover:-translate-y-1 flex flex-col justify-between">
      <div>
        {/* Image Container */}
        <div className="relative aspect-square overflow-hidden bg-black/40">
          <img
            src={product.imageUrl}
            alt={product.name}
            className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-108"
          />
          {product.purityBadge && (
            <span className="absolute top-3 left-3 bg-black/70 border border-gold-500/30 text-gold-300 text-[10px] uppercase font-bold tracking-widest px-2.5 py-1 rounded backdrop-blur-md">
              {product.purityBadge}
            </span>
          )}
          <button
            onClick={() =>
              toggleWishlist({
                id: product.id,
                name: product.name,
                slug: product.slug,
                price: product.price,
                imageUrl: product.imageUrl,
                purityBadge: product.purityBadge,
              })
            }
            className={`absolute top-3 right-3 p-2 rounded-full backdrop-blur-md transition-colors ${
              isWishlisted
                ? 'bg-rose-500/20 text-rose-500 border border-rose-500/40'
                : 'bg-black/50 text-white hover:text-rose-500'
            }`}
          >
            <Heart className={`w-4 h-4 ${isWishlisted ? 'fill-current' : ''}`} />
          </button>
        </div>

        {/* Content */}
        <div className="p-5">
          <span className="text-[10px] uppercase font-bold tracking-widest text-gold-400">
            {product.categoryName}
          </span>
          <Link href={`/product/${product.slug}`}>
            <h3 className="font-serif text-base font-semibold text-gray-100 group-hover:text-gold-300 transition-colors line-clamp-1 mt-1">
              {product.name}
            </h3>
          </Link>

          {/* Rating */}
          <div className="flex items-center gap-1 mt-2 text-xs text-amber-400">
            <Star className="w-3.5 h-3.5 fill-current" />
            <span className="font-semibold text-gray-200">{product.rating || 4.9}</span>
            <span className="text-gray-500">({product.reviewCount || 48})</span>
          </div>

          {/* Price */}
          <div className="flex items-baseline gap-2 mt-3">
            <span className="font-serif text-lg font-bold text-white">
              ₹{product.price.toLocaleString('en-IN')}
            </span>
            {product.originalPrice && (
              <span className="text-xs text-gray-500 line-through">
                ₹{product.originalPrice.toLocaleString('en-IN')}
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Action Button */}
      <div className="px-5 pb-5">
        <button
          onClick={() =>
            addItem({
              productId: product.id,
              sku: product.sku,
              productName: product.name,
              imageUrl: product.imageUrl,
              unitPrice: product.price,
              quantity: 1,
            })
          }
          className="w-full py-2.5 bg-white/5 border border-white/10 text-xs font-semibold tracking-wider text-gray-200 rounded hover:bg-gold-500 hover:text-black hover:border-gold-500 transition-all flex items-center justify-center gap-2"
        >
          <ShoppingBag className="w-3.5 h-3.5" />
          <span>ADD TO BAG</span>
        </button>
      </div>
    </div>
  );
};
