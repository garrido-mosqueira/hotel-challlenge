export async function parseErrorMessage(response: Response, defaultMessage: string): Promise<string> {
  try {
    const text = await response.text();
    try {
      const data = JSON.parse(text) as Record<string, unknown>;
      const message = data.message;
      const error = data.error;

      if (typeof message === 'string' && message) return message;
      if (typeof error === 'string' && error) return error;
      return defaultMessage;
    } catch {
      return text || response.statusText || defaultMessage;
    }
  } catch {
    return defaultMessage;
  }
}

