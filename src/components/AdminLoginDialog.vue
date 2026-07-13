<script setup>
import { ref, watch, nextTick } from 'vue'

const props = defineProps({
  open: { type: Boolean, required: true },
  loginFn: { type: Function, required: true },
})

const emit = defineEmits(['close'])

const password = ref('')
const error = ref(null)
const submitting = ref(false)
const showPassword = ref(false)
const inputEl = ref(null)

watch(
  () => props.open,
  async (open) => {
    if (open) {
      password.value = ''
      error.value = null
      await nextTick()
      inputEl.value?.focus()
    }
  },
)

async function handleSubmit() {
  if (!password.value || submitting.value) return
  submitting.value = true
  error.value = null
  try {
    await props.loginFn(password.value)
    emit('close')
  } catch (e) {
    error.value = e.message
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <Teleport to="body">
    <div v-if="open" class="overlay" @click.self="emit('close')">
      <div class="dialog" role="dialog" aria-modal="true" aria-labelledby="login-title">
        <h2 id="login-title" class="title">Acesso do administrador</h2>

        <form class="form" @submit.prevent="handleSubmit">
          <label class="label" for="admin-password">Senha</label>
          <div class="input-wrap">
            <input
              id="admin-password"
              ref="inputEl"
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              class="input"
              autocomplete="current-password"
              required
            />
            <button
              type="button"
              class="toggle"
              :aria-label="showPassword ? 'Ocultar senha' : 'Mostrar senha'"
              @click="showPassword = !showPassword"
            >
              {{ showPassword ? 'Ocultar' : 'Mostrar' }}
            </button>
          </div>

          <p v-if="error" class="error" role="alert">{{ error }}</p>

          <div class="actions">
            <button type="button" class="btn btn--ghost" @click="emit('close')">
              Cancelar
            </button>
            <button type="submit" class="btn btn--primary" :disabled="submitting">
              {{ submitting ? 'Entrando…' : 'Entrar' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </Teleport>
</template>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.55);
  display: grid;
  place-items: center;
  padding: var(--space-4);
  z-index: 100;
}

.dialog {
  width: 100%;
  max-width: 380px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-lg);
  padding: var(--space-6);
  box-shadow: var(--shadow-card);
}

.title {
  font-size: 1.25rem;
  margin-bottom: var(--space-4);
}

.form {
  display: flex;
  flex-direction: column;
  gap: var(--space-3);
}

.label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-muted);
}

.input-wrap {
  display: flex;
  gap: var(--space-2);
}

.input {
  flex: 1;
  min-height: 44px;
  padding: var(--space-2) var(--space-3);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  background-color: var(--color-background);
  color: var(--color-foreground);
  font-size: 1rem;
}

.input:focus-visible {
  outline: 3px solid var(--color-accent);
  outline-offset: 1px;
}

.toggle {
  min-height: 44px;
  padding: 0 var(--space-3);
  border-radius: var(--radius-sm);
  border: 1px solid var(--color-border);
  color: var(--color-muted);
  font-size: 0.8125rem;
}

.error {
  color: #f87171;
  font-size: 0.875rem;
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--space-2);
  margin-top: var(--space-2);
}

.btn {
  min-height: 44px;
  padding: var(--space-2) var(--space-4);
  border-radius: var(--radius-sm);
  font-weight: 600;
  transition: background-color var(--transition-fast);
}

.btn--ghost {
  color: var(--color-muted);
  border: 1px solid var(--color-border);
}

.btn--primary {
  background-color: var(--color-accent);
  color: #fff;
}

.btn--primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
