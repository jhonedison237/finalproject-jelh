import { render, screen } from '@testing-library/react';
import { Card, CardHeader, CardTitle, CardBody, CardFooter } from '@/components/common/Card';

describe('Card Components', () => {
  describe('Card', () => {
    it('should render children', () => {
      render(<Card>Card Content</Card>);
      expect(screen.getByText('Card Content')).toBeInTheDocument();
    });

    it('should apply custom className', () => {
      const { container } = render(<Card className="custom-card">Content</Card>);
      const card = container.firstChild;
      expect(card).toHaveClass('custom-card');
    });

    it('should have base card styles', () => {
      const { container } = render(<Card>Content</Card>);
      const card = container.firstChild;
      expect(card).toHaveClass('bg-white', 'rounded-xl');
    });
  });

  describe('CardHeader', () => {
    it('should render children', () => {
      render(<CardHeader>Header Content</CardHeader>);
      expect(screen.getByText('Header Content')).toBeInTheDocument();
    });

    it('should have header styles', () => {
      const { container } = render(<CardHeader>Header</CardHeader>);
      const header = container.firstChild;
      expect(header).toHaveClass('px-6', 'py-4');
    });

    it('should apply custom className', () => {
      const { container } = render(<CardHeader className="custom-header">Header</CardHeader>);
      const header = container.firstChild;
      expect(header).toHaveClass('custom-header');
    });
  });

  describe('CardTitle', () => {
    it('should render children', () => {
      render(<CardTitle>Title</CardTitle>);
      expect(screen.getByText('Title')).toBeInTheDocument();
    });

    it('should render as h3 element', () => {
      render(<CardTitle>Title</CardTitle>);
      const title = screen.getByText('Title');
      expect(title.tagName).toBe('H3');
    });

    it('should have title styles', () => {
      render(<CardTitle>Title</CardTitle>);
      const title = screen.getByText('Title');
      expect(title).toHaveClass('text-lg', 'font-bold', 'text-gray-900');
    });
  });

  describe('CardBody', () => {
    it('should render children', () => {
      render(<CardBody>Body Content</CardBody>);
      expect(screen.getByText('Body Content')).toBeInTheDocument();
    });

    it('should have body styles', () => {
      const { container } = render(<CardBody>Body</CardBody>);
      const body = container.firstChild;
      expect(body).toHaveClass('px-6', 'py-4');
    });
  });

  describe('CardFooter', () => {
    it('should render children', () => {
      render(<CardFooter>Footer Content</CardFooter>);
      expect(screen.getByText('Footer Content')).toBeInTheDocument();
    });

    it('should have footer styles', () => {
      const { container } = render(<CardFooter>Footer</CardFooter>);
      const footer = container.firstChild;
      expect(footer).toHaveClass('px-6', 'py-4');
    });
  });

  describe('Full Card Composition', () => {
    it('should render complete card with all parts', () => {
      render(
        <Card>
          <CardHeader>
            <CardTitle>Test Title</CardTitle>
          </CardHeader>
          <CardBody>Test Body</CardBody>
          <CardFooter>Test Footer</CardFooter>
        </Card>
      );

      expect(screen.getByText('Test Title')).toBeInTheDocument();
      expect(screen.getByText('Test Body')).toBeInTheDocument();
      expect(screen.getByText('Test Footer')).toBeInTheDocument();
    });
  });
});

