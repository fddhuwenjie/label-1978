const SESSION_KEY = 'student_session'

export function saveSession(data) {
  const session = {
    ...data,
    savedAt: Date.now()
  }
  localStorage.setItem(SESSION_KEY, JSON.stringify(session))
}

export function getSession() {
  const data = localStorage.getItem(SESSION_KEY)
  if (!data) return null
  
  try {
    const session = JSON.parse(data)
    const timeoutSeconds = session.expiresIn || 20
    const elapsed = (Date.now() - session.savedAt) / 1000
    if (elapsed > timeoutSeconds) {
      clearSession()
      return null
    }
    return session
  } catch {
    return null
  }
}

export function refreshSession() {
  const session = getSession()
  if (session) {
    session.savedAt = Date.now()
    localStorage.setItem(SESSION_KEY, JSON.stringify(session))
  }
}

export function clearSession() {
  localStorage.removeItem(SESSION_KEY)
}

export function isAdmin() {
  const session = getSession()
  return session && session.role === 'admin'
}
