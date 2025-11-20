import { render, screen, fireEvent } from '@testing-library/react';
import { Button } from '@/components/common/Button';

describe('Button Component', () => {
  it('should render with children', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('should call onClick when clicked', () => {
    const handleClick = jest.fn();
    render(<Button onClick={handleClick}>Click me</Button>);
    
    fireEvent.click(screen.getByText('Click me'));
    expect(handleClick).toHaveBeenCalledTimes(1);
  });

  it('should apply primary variant styles by default', () => {
    render(<Button>Primary</Button>);
    const button = screen.getByText('Primary');
    expect(button).toHaveClass('bg-gradient-to-r');
  });

  it('should apply secondary variant styles', () => {
    render(<Button variant="secondary">Secondary</Button>);
    const button = screen.getByText('Secondary');
    expect(button).toHaveClass('bg-gradient-to-r');
  });

  it('should apply success variant styles', () => {
    render(<Button variant="success">Success</Button>);
    const button = screen.getByText('Success');
    expect(button).toHaveClass('bg-gradient-to-r');
  });

  it('should apply danger variant styles', () => {
    render(<Button variant="danger">Danger</Button>);
    const button = screen.getByText('Danger');
    expect(button).toHaveClass('bg-gradient-to-r');
  });

  it('should apply outline variant styles', () => {
    render(<Button variant="outline">Outline</Button>);
    const button = screen.getByText('Outline');
    expect(button).toHaveClass('border-2');
  });

  it('should apply ghost variant styles', () => {
    render(<Button variant="ghost">Ghost</Button>);
    const button = screen.getByText('Ghost');
    expect(button).toHaveClass('text-gray-700');
  });

  it('should apply small size', () => {
    render(<Button size="sm">Small</Button>);
    const button = screen.getByText('Small');
    expect(button).toHaveClass('px-3', 'py-1.5', 'text-sm');
  });

  it('should apply medium size by default', () => {
    render(<Button>Medium</Button>);
    const button = screen.getByText('Medium');
    expect(button).toHaveClass('px-4', 'py-2', 'text-base');
  });

  it('should apply large size', () => {
    render(<Button size="lg">Large</Button>);
    const button = screen.getByText('Large');
    expect(button).toHaveClass('px-6', 'py-3', 'text-lg');
  });

  it('should be disabled when disabled prop is true', () => {
    render(<Button disabled>Disabled</Button>);
    const button = screen.getByText('Disabled');
    expect(button).toBeDisabled();
    expect(button).toHaveClass('disabled:opacity-50', 'disabled:cursor-not-allowed');
  });

  it('should show loading spinner when loading', () => {
    render(<Button loading>Loading</Button>);
    const button = screen.getByText('Loading');
    const svg = button.querySelector('svg');
    expect(svg).toBeInTheDocument();
    expect(svg).toHaveClass('animate-spin');
  });

  it('should be disabled when loading', () => {
    const handleClick = jest.fn();
    render(<Button loading onClick={handleClick}>Loading</Button>);
    const button = screen.getByText('Loading');
    
    expect(button).toBeDisabled();
    fireEvent.click(button);
    expect(handleClick).not.toHaveBeenCalled();
  });

  it('should apply custom className', () => {
    render(<Button className="custom-class">Custom</Button>);
    const button = screen.getByText('Custom');
    expect(button).toHaveClass('custom-class');
  });

  it('should render as button type by default', () => {
    render(<Button>Button</Button>);
    const button = screen.getByText('Button');
    expect(button).toHaveAttribute('type', 'button');
  });

  it('should render as submit type when specified', () => {
    render(<Button type="submit">Submit</Button>);
    const button = screen.getByText('Submit');
    expect(button).toHaveAttribute('type', 'submit');
  });
});

