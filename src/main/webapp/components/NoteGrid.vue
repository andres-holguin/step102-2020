<template>
  <v-container id="note-grid">
    <!-- A single preview component that shows the noteToPreview-->
    <note-preview v-bind="noteToPreview"
                  :is-favorited="isFavorited">
    </note-preview>
    
    <!-- Component that sets filters for search results -->
    <filter-list v-model="filters"></filter-list>
    <!-- TODO: Add filters for age -->

    <!-- Slot for all the <note-grid-collection> components -->
    <slot :filters="filters"></slot>
  </v-container>
</template>

<script>
module.exports = {
  components: {
    'note-preview': httpVueLoader('/components/NotePreview.vue'),
    'filter-list': httpVueLoader('/components/FilterList.vue')
  },
  data: function() {
    return {
      isFavorited: true,
      noteToPreview: {},
      filters: [], // Rename to distinguish from below
      // TODO: Add filters for age
    }
  },
  mounted: function() {
    this.$on('open-preview', note => {
      this.noteToPreview = note;
    });
  }
}
</script>
