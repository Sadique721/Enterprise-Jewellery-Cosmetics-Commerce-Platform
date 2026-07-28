import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        gold: {
          50: '#fffbf0',
          100: '#fef3d6',
          200: '#fce3a8',
          300: '#f9cb71',
          400: '#f6aa3c',
          500: '#e58e17',
          600: '#ca6e0f',
          700: '#a34e10',
          800: '#853e14',
          900: '#6f3414',
        },
        ruby: {
          900: '#4a0404',
          800: '#7a0c0c',
          700: '#991111',
        },
        rose: {
          50: '#fff5f7',
          100: '#ffe6ec',
          200: '#fccad7',
          500: '#f43f5e',
        },
        champagne: '#f7e7ce',
        maroon: '#4a0e17',
        navy: '#0b132b',
      },
      fontFamily: {
        serif: ['Playfair Display', 'Georgia', 'serif'],
        sans: ['Inter', 'sans-serif'],
      },
    },
  },
  plugins: [],
};
export default config;
