<template>
  <v-form ref="form" @submit.prevent="onSubmit">
    <v-row>
      <v-col>
        <v-file-input label="Upload a PDF"
                      v-model="file"
                      accept=".pdf"
                      show-size
                      @change="onFileChange">
        </v-file-input>

        <v-text-field label="Title"
                      v-model="title"
                      clearable>
        </v-text-field>

        <v-combobox label="School"
                    v-model="school"
                    ref="school"
                    clearable chips
                    :items="commonSchools">
          <template v-slot:no-data>
            <span v-html="noDataHtml"></span>
          </template>
        </v-combobox>

        <v-combobox label="Course"
                    v-model="course"
                    ref="course"
                    clearable chips
                    :items="commonCourses">
          <template v-slot:no-data>
            <span v-html="noDataHtml"></span>
          </template>
        </v-combobox>
      </v-col>

      <v-col>
        <iframe :src="previewUrl"></iframe>
      </v-col>
    </v-row>

    <v-combobox label="Miscellaneous Labels"
                v-model="miscLabels"
                multiple chips deletable-chips
                clearable hide-selected
                :items="commonLabels">
      <template v-slot:no-data>
        <span v-html="noDataHtml"></span>
      </template>
    </v-combobox>

    <v-btn type="submit">Post Note</v-btn>
  </v-form>
</template>

<script>
module.exports = {
  data: function() {
    return {
      uploadUrl: null,
      file: null,
      previewUrl: null,
      title: null,
      school: "Unaffiliated",
      commonSchools: ['UF', 'MIT', 'NYU', 'UCLA'],
      course: "Unaffiliated",
      commonCourses: ['CIS4301', 'ENC1101', 'PHY2049'],
      miscLabels: null,
      commonLabels: ['Hard', 'Professor X', 'Test Prep'],
      noDataHtml: "No matching results. Type and press <kbd>enter</kbd> to create a new one",
    }
  },
  watch: {
    // Blur input fields for school and course when a selection is made.
    // Communicates to the user that their custom input is selected.
    // Must wait for next tick to avoid race conditions.
    school: function() {
      this.nextTick(() => this.$refs.school.blur());
    },
    course: function() {
      this.nextTick(() => this.$refs.course.blur());
    },
  },
  methods: {
    nextTick: function(f) {
      // Avoids race conditions with function f
      setTimeout(f, 0);
    },
    onFileChange: function(file) {
      // Revoke old preview url
      URL.revokeObjectURL(this.previewUrl);
      this.previewUrl = null;
      if (file) {
        // Set new preview url
        this.previewUrl = URL.createObjectURL(file);
        // Autofill Title field
        this.title = file.name.replace(".pdf", "");
      }
    },
    onSubmit: function() {
      const formData = new FormData();
      formData.append('file', this.file);
      formData.append('title', this.title);
      formData.append('school', this.school);
      formData.append('course', this.course);
      formData.append('miscLabels', this.miscLabels)

      fetch(this.uploadUrl, {
        method: 'POST',
        body: formData,
      }).then(this.reset());
    },
    reset: function() {
      this.$refs.form.reset()
    }, 
  },
  mounted: function() {
    // Get new upload url for the form
    fetch('/blobstore-upload-url')
      .then(response => response.text())
      .then(url => this.uploadUrl = url);
  }
}
</script>

<style>
iframe {
  height: 100%;
}
</style>