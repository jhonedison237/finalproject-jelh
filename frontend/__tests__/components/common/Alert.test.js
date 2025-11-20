import { render, screen, fireEvent } from '@testing-library/react';
import { Alert } from '@/components/common/Alert';

describe('Alert Component', () => {
  it('should render with message', () => {
    render(<Alert message="Test message" />);
    expect(screen.getByText('Test message')).toBeInTheDocument();
  });

  it('should render with title and message', () => {
    render(<Alert title="Test Title" message="Test message" />);
    expect(screen.getByText('Test Title')).toBeInTheDocument();
    expect(screen.getByText('Test message')).toBeInTheDocument();
  });

  it('should render success type with correct icon', () => {
    const { container } = render(<Alert type="success" message="Success" />);
    const alert = container.firstChild;
    expect(alert).toHaveClass('bg-green-50', 'border-green-200');
  });

  it('should render error type with correct icon', () => {
    const { container } = render(<Alert type="error" message="Error" />);
    const alert = container.firstChild;
    expect(alert).toHaveClass('bg-red-50', 'border-red-200');
  });

  it('should render warning type with correct icon', () => {
    const { container } = render(<Alert type="warning" message="Warning" />);
    const alert = container.firstChild;
    expect(alert).toHaveClass('bg-yellow-50', 'border-yellow-200');
  });

  it('should render info type with correct icon by default', () => {
    const { container } = render(<Alert message="Info" />);
    const alert = container.firstChild;
    expect(alert).toHaveClass('bg-blue-50', 'border-blue-200');
  });

  it('should show close button when onClose is provided', () => {
    const handleClose = jest.fn();
    render(<Alert message="Test" onClose={handleClose} />);
    const closeButton = screen.getByText('✕');
    expect(closeButton).toBeInTheDocument();
  });

  it('should call onClose when close button is clicked', () => {
    const handleClose = jest.fn();
    render(<Alert message="Test" onClose={handleClose} />);
    const closeButton = screen.getByText('✕');
    
    fireEvent.click(closeButton);
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  it('should not show close button when onClose is not provided', () => {
    render(<Alert message="Test" />);
    const closeButton = screen.queryByText('✕');
    expect(closeButton).not.toBeInTheDocument();
  });

  it('should apply custom className', () => {
    const { container } = render(<Alert message="Test" className="custom-alert" />);
    const alert = container.firstChild;
    expect(alert).toHaveClass('custom-alert');
  });

  it('should render only message without title', () => {
    render(<Alert message="Only message" />);
    expect(screen.getByText('Only message')).toBeInTheDocument();
    expect(screen.queryByRole('heading')).not.toBeInTheDocument();
  });
});

