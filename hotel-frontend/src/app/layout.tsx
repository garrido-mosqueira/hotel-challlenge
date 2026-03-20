import type { Metadata } from 'next'

export const metadata: Metadata = {
  title: 'Hotel Booking',
  description: 'A frontend to test the Hotel API',
}

export default function RootLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <html lang="en">
      <body>
        <main>{children}</main>
      </body>
    </html>
  )
}
