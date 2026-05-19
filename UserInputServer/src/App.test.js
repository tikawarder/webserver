import { render, screen } from '@testing-library/react';
import App from './App';

test('renders login header', () => {
  render(<App />);
  const loginHeader = screen.getByRole('heading', { name: /login/i });
  expect(loginHeader).toBeInTheDocument();
});
