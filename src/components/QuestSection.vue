<script setup>
import QuestCards from './QuestCards.vue'
import MissionsList from './MissionsList.vue'
import { useRevealOnScroll } from '../composables/useRevealOnScroll'

defineProps({
  groups: { type: Array, default: () => [] },
})

const { targetRef, revealed } = useRevealOnScroll()
</script>

<template>
  <section ref="targetRef" class="quest-section" :class="{ 'quest-section--revealed': revealed }" aria-label="Sistema de quests">
    <div class="quest-header">
      <h2 class="quest-heading">Sistema de Quests</h2>
      <p class="quest-subheading">Complete missões, acumule pontos, conquiste a vitória</p>
    </div>

    <QuestCards />

    <MissionsList :groups="groups" />
  </section>
</template>

<style scoped>
.quest-section {
  position: relative;
  background-color: var(--color-background);
  padding: var(--space-16) var(--space-4);
  display: flex;
  flex-direction: column;
  gap: var(--space-12);
  max-width: 1200px;
  margin: 0 auto;
}

.quest-section::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0.03;
  pointer-events: none;
  background-image: radial-gradient(rgba(255, 255, 255, 0.8) 1px, transparent 1px);
  background-size: 3px 3px;
  z-index: 0;
}

.quest-header,
.quest-section > :deep(.quest-grid),
.quest-section > :deep(.missions) {
  position: relative;
  z-index: 1;
}

.quest-header {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: var(--space-2);
  opacity: 0;
  transform: translateY(24px);
  transition: opacity 0.6s ease, transform 0.6s ease;
}

.quest-section--revealed .quest-header {
  opacity: 1;
  transform: translateY(0);
}

.quest-heading {
  font-size: 2.2rem;
  font-weight: 800;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.quest-subheading {
  color: var(--color-muted);
  font-size: 1.05rem;
}

.quest-section :deep(.quest-card) {
  animation-play-state: paused;
}

.quest-section--revealed :deep(.quest-card) {
  animation-play-state: running;
}
</style>
