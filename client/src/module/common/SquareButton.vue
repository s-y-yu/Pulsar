<template>
  <button
    type="button"
    class="btn"
    :style="styles"
    @click="handleClick"
  >
    {{ value ? value : 'example' }}
  </button>
</template>

<style scoped>
@import url('../../assets/css/init.css');
@import url('../../assets/css/root.css');
@import url('../../assets/css/typography.css');

button {
  cursor: pointer;
  background-color: var(--color);
  padding: 0.75rem 1.25rem 0.75rem 1.25rem;
  border: var(--border);
  border-radius: 8px;
  color: var(--text);
  width: 100%;
  font-size: 0.9rem;
  font-family: 'bd', sans-serif;
}

button:hover {
  opacity: 0.7;
}
</style>

<script>
export default {
  name: 'SquareButton',
  props: { theme: String, value: String },
  methods: {
    handleClick(event) {
      this.$emit('handle-click');
    },
  },
  computed: {
    styles() {
      if (this.theme === 'black') {
        return {
          '--color': '#333333',
          '--border': '1px solid #333333',
          '--text': 'white',
        };
      } else if (this.theme === 'point') {
        return {
          '--color': '#ff8048',
          '--border': '1px solid #ff8048',
          '--text': 'white',
        };
      } else if (this.theme === 'highlight') {
        return {
          '--color': '#db3f28',
          '--border': '1px solid #db3f28',
          '--text': 'white',
        };
      } else
        return {
          '--color': 'white',
          '--border': '1px solid #333333',
          '--text': '#333333',
        };
    },
  },
};
</script>
