'use client';

import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export interface WishlistItem {
  id: string;
  name: string;
  slug: string;
  price: number;
  imageUrl: string;
  purityBadge?: string;
}

interface WishlistStore {
  items: WishlistItem[];
  toggleWishlist: (item: WishlistItem) => void;
  isInWishlist: (id: string) => boolean;
  removeItem: (id: string) => void;
  clearWishlist: () => void;
}

export const useWishlistStore = create<WishlistStore>()(
  persist(
    (set, get) => ({
      items: [
        {
          id: '1',
          name: 'Royal Heritage Kundan Choker Necklace',
          slug: 'royal-heritage-kundan-choker',
          price: 185000,
          imageUrl: 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?q=80&w=800&auto=format&fit=crop',
          purityBadge: '22K Hallmarked Gold',
        },
        {
          id: '2',
          name: 'Solitaire Diamond Engagement Ring',
          slug: 'solitaire-diamond-ring',
          price: 145000,
          imageUrl: 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?q=80&w=800&auto=format&fit=crop',
          purityBadge: 'VVS1 IGI Certified',
        },
      ],
      toggleWishlist: (item) => {
        const exists = get().items.some((i) => i.id === item.id);
        if (exists) {
          set({ items: get().items.filter((i) => i.id !== item.id) });
        } else {
          set({ items: [...get().items, item] });
        }
      },
      isInWishlist: (id) => get().items.some((i) => i.id === id),
      removeItem: (id) => set({ items: get().items.filter((i) => i.id !== id) }),
      clearWishlist: () => set({ items: [] }),
    }),
    {
      name: 'sanab-wishlist-storage',
    }
  )
);
