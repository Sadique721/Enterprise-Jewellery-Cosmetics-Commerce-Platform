import { create } from 'zustand';

export interface CartItem {
  id: string;
  productId: string;
  sku: string;
  productName: string;
  imageUrl: string;
  unitPrice: number;
  quantity: number;
}

interface CartStore {
  items: CartItem[];
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  addItem: (item: Omit<CartItem, 'id'>) => void;
  removeItem: (sku: string) => void;
  updateQuantity: (sku: string, quantity: number) => void;
  clearCart: () => void;
  getTotal: () => number;
}

export const useCartStore = create<CartStore>((set, get) => ({
  items: [],
  isOpen: false,
  setIsOpen: (isOpen) => set({ isOpen }),

  addItem: (newItem) => {
    set((state) => {
      const existing = state.items.find((i) => i.sku === newItem.sku);
      if (existing) {
        return {
          items: state.items.map((i) =>
            i.sku === newItem.sku ? { ...i, quantity: i.quantity + newItem.quantity } : i
          ),
          isOpen: true,
        };
      }
      return {
        items: [...state.items, { ...newItem, id: Math.random().toString() }],
        isOpen: true,
      };
    });
  },

  removeItem: (sku) => {
    set((state) => ({
      items: state.items.filter((i) => i.sku !== sku),
    }));
  },

  updateQuantity: (sku, quantity) => {
    if (quantity <= 0) {
      get().removeItem(sku);
      return;
    }
    set((state) => ({
      items: state.items.map((i) => (i.sku === sku ? { ...i, quantity } : i)),
    }));
  },

  clearCart: () => set({ items: [] }),

  getTotal: () => {
    return get().items.reduce((total, item) => total + item.unitPrice * item.quantity, 0);
  },
}));
