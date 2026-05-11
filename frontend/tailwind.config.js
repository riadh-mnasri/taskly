/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts,scss}"],
  theme: {
    extend: {
      colors: {
        primary: {
          50:  '#f5f3ff',
          100: '#ede9fe',
          200: '#ddd6fe',
          300: '#c4b5fd',
          400: '#a78bfa',
          500: '#8b5cf6',
          600: '#7c3aed',
          700: '#6d28d9',
          800: '#5b21b6',
          900: '#4c1d95',
        },
        priority: {
          high:   '#ef4444',
          medium: '#f97316',
          low:    '#10b981',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        'card':       '0 4px 24px rgba(99,102,241,0.10)',
        'card-hover': '0 8px 32px rgba(99,102,241,0.18)',
      }
    },
  },
  plugins: [],
  corePlugins: { preflight: false }
}
