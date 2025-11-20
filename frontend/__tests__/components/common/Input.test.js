import { render, screen, fireEvent } from '@testing-library/react';
import { Input } from '@/components/common/Input';

describe('Input Component', () => {
  it('should render input element', () => {
    render(<Input />);
    const input = screen.getByRole('textbox');
    expect(input).toBeInTheDocument();
  });

  it('should render with label', () => {
    render(<Input label="Test Label" />);
    expect(screen.getByText('Test Label')).toBeInTheDocument();
  });

  it('should show required asterisk when required', () => {
    render(<Input label="Email" required />);
    expect(screen.getByText('*')).toBeInTheDocument();
  });

  it('should display error message', () => {
    render(<Input error="This field is required" />);
    expect(screen.getByText('This field is required')).toBeInTheDocument();
  });

  it('should display helper text', () => {
    render(<Input helperText="Enter your email address" />);
    expect(screen.getByText('Enter your email address')).toBeInTheDocument();
  });

  it('should not show helper text when error is present', () => {
    render(
      <Input 
        helperText="Helper text" 
        error="Error message" 
      />
    );
    expect(screen.queryByText('Helper text')).not.toBeInTheDocument();
    expect(screen.getByText('Error message')).toBeInTheDocument();
  });

  it('should apply error styles when error exists', () => {
    render(<Input error="Error" />);
    const input = screen.getByRole('textbox');
    expect(input).toHaveClass('border-danger-500');
  });

  it('should handle onChange event', () => {
    const handleChange = jest.fn();
    render(<Input onChange={handleChange} />);
    const input = screen.getByRole('textbox');
    
    fireEvent.change(input, { target: { value: 'test' } });
    expect(handleChange).toHaveBeenCalled();
  });

  it('should handle different input types', () => {
    const { rerender } = render(<Input type="text" />);
    let input = screen.getByRole('textbox');
    expect(input).toHaveAttribute('type', 'text');

    rerender(<Input type="email" />);
    input = screen.getByRole('textbox');
    expect(input).toHaveAttribute('type', 'email');
  });

  it('should be disabled when disabled prop is true', () => {
    render(<Input disabled />);
    const input = screen.getByRole('textbox');
    expect(input).toBeDisabled();
    expect(input).toHaveClass('disabled:bg-gray-100', 'disabled:cursor-not-allowed');
  });

  it('should apply custom className', () => {
    render(<Input className="custom-input" />);
    const input = screen.getByRole('textbox');
    expect(input).toHaveClass('custom-input');
  });

  it('should apply custom containerClassName', () => {
    render(<Input containerClassName="custom-container" label="Test" />);
    const container = screen.getByText('Test').parentElement;
    expect(container).toHaveClass('custom-container');
  });

  it('should set placeholder', () => {
    render(<Input placeholder="Enter text here" />);
    const input = screen.getByPlaceholderText('Enter text here');
    expect(input).toBeInTheDocument();
  });

  it('should set value', () => {
    render(<Input value="test value" readOnly />);
    const input = screen.getByDisplayValue('test value');
    expect(input).toBeInTheDocument();
  });
});

