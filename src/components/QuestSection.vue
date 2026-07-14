<script setup>
import QuestCards from './QuestCards.vue'
import MissionsList from './MissionsList.vue'
import { useRevealOnScroll } from '../composables/useRevealOnScroll'

defineProps({
  groups: { type: Array, default: () => [] },
})

const { targetRef: questsRef, revealed: questsRevealed } = useRevealOnScroll()
const { targetRef: missionsRef, revealed: missionsRevealed } = useRevealOnScroll()
</script>

<template>
  <section ref="questsRef" class="sect" aria-label="Sistema de quests">
    <h2 class="st rv" :class="{ v: questsRevealed }">Sistema de Quests</h2>
    <p class="ss rv" :class="{ v: questsRevealed }">
      Complete missões, acumule pontos, conquiste a vitória
    </p>
    <QuestCards :revealed="questsRevealed" />
  </section>

  <section ref="missionsRef" class="sect" aria-label="Missões de lançamento">
    <h2 class="st rv" :class="{ v: missionsRevealed }">Missões de Lançamento</h2>
    <p class="ss rv" :class="{ v: missionsRevealed }">
      Cumpra primeiro e largue na frente com pontos extras
    </p>
    <MissionsList :groups="groups" :revealed="missionsRevealed" />
  </section>
</template>

<style scoped>
.sect {
  padding: 70px 20px;
  background-color: var(--color-background);
}

.st {
  font-family: var(--font-display);
  font-size: 1.4rem;
  font-weight: 800;
  letter-spacing: 4px;
  text-transform: uppercase;
  text-align: center;
  margin-bottom: 6px;
  color: var(--color-foreground);
}

.ss {
  font-size: 0.85rem;
  font-weight: 500;
  text-align: center;
  color: var(--color-muted);
  margin-bottom: 44px;
}

.rv {
  opacity: 0;
  transform: translateY(18px);
  transition: opacity 0.5s ease, transform 0.5s ease;
}

.rv.v {
  opacity: 1;
  transform: translateY(0);
}
</style>
