<template>
  <div class="exercise_list_container">
    <div v-if="exercise" class="exercise_list_item">
      <p>
        <strong>
          {{ exercise.exerciseName ? exercise.exerciseName : ' --- ' }}
        </strong>
        {{ exercise.count ? exercise.count + '회' : '' }}
        {{ exercise.count && exercise.duration ? ' / ' : '' }}
        {{ exercise.duration ? exercise.duration + '분' : '' }}
      </p>
    </div>
    <div v-else>
      <p>등록한 운동이 없어요.</p>
    </div>
  </div>
</template>

<script>
export default {
  name: 'exerciseList',
  props: {
    exercise: Object,
  },
};
</script>

<style scoped>
@import url('../../assets/css/init.css');
@import url('../../assets/css/root.css');
@import url('../../assets/css/typography.css');

.exercise_list_container {
  border: 1px solid var(--light-color-grey);
  border-radius: 8px;
  padding: 0.75rem;
  margin-bottom: 1rem;
}
h3 {
  color: var(--light-color-black);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 0.5rem;
}
</style>
