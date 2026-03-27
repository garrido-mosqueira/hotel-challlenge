type QueryValue = string | number | boolean | null | undefined;

type ApiRequestOptions = RequestInit & {
  query?: Record<string, QueryValue>;
};

function withQuery(path: string, query?: Record<string, QueryValue>): string {
  if (!query) return path;

  const searchParams = new URLSearchParams();
  Object.entries(query).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      searchParams.set(key, String(value));
    }
  });

  const queryString = searchParams.toString();
  return queryString ? `${path}?${queryString}` : path;
}

async function request(path: string, options: ApiRequestOptions = {}): Promise<Response> {
  const { query, ...fetchOptions } = options;
  return fetch(withQuery(path, query), fetchOptions);
}

export const apiClient = {
  request,
  get(path: string, options: Omit<ApiRequestOptions, 'method' | 'body'> = {}) {
    return request(path, { ...options, method: 'GET' });
  },
  post(path: string, body?: unknown, options: Omit<ApiRequestOptions, 'method' | 'body'> = {}) {
    return request(path, {
      ...options,
      method: 'POST',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  },
  put(path: string, body?: unknown, options: Omit<ApiRequestOptions, 'method' | 'body'> = {}) {
    return request(path, {
      ...options,
      method: 'PUT',
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  },
  delete(path: string, options: Omit<ApiRequestOptions, 'method'> = {}) {
    return request(path, { ...options, method: 'DELETE' });
  },
};

