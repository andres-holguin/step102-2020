<template>
  <div>
    <v-container fluid class="no-pad wave">
      <v-row>
        <v-col class="text-center" cols="12">
          <v-spacer class="lg-spacer"></v-spacer>
          <h1 class="font-weight-bold display-4 white-text"
              id="starfish-title">
            starfish
            <img src="/assets/starfish.png"
                 id="starfish-logo">
          </h1>
          <v-spacer></v-spacer>
          <h1 class="display-1 white-text"
              id="starfish-subtitle">
            Share and find the best course notes. Completely free.
          </h1>
          <v-spacer class="md-spacer"></v-spacer>
          <v-row align="center" justify="center">
            <search-bar :is-dark="true" :show-search-button="false"></search-bar>
          </v-row>
        </v-col>
      </v-row>
      <v-row align="center" justify="center">
        <v-img src="/assets/laptop-min.png"
               id="laptop-img">
        </v-img>
      </v-row>
    </v-container>
    <v-container fluid id="info-section">
      <v-row align="center" justify="space-around">
        <v-col sm="6">
          <p class="heading-1">
            Quickly identify course notes relevant to you.
          </p>
          <p class="subheading-1">
            Wish you could find high quality notes for all your classes? Look no further - 
            start by searching for your school and/or course and  
            then filter your results to find exactly what you're looking for. 
            We rank your search results based on what's trending among other users so you 
            only see the notes most relevant to you. 
            Oh, and did we mention that it's 100% free?
          </p>
        </v-col>
        <v-col sm="4">
          <v-img src="/assets/search.svg"></v-img>
        </v-col>
      </v-row>
      <v-row align="center" justify="space-around">
        <v-col cols="4">
          <v-img src="/assets/share.svg"></v-img>
        </v-col>
        <v-col sm="6">
          <p class="heading-1">
            Earn points for the notes you share.
          </p>
          <p class="subheading-1">
            Do you ever feel like your notes go to waste once classes are finished?
            Wish you could easily share your notes with your peers? Now you can - just log in 
            with your Google account to get started! Even better, earn
            points as you get more downloads and favorites on your uploaded notes. <br><br>
            <em>Coming soon: Redeem your points for awesome rewards!</em>
          </p>
        </v-col>
      </v-row>
      <v-row align="center" justify="space-around">
        <v-col sm="6">
          <p class="heading-1">
            Increase the accessibility of learning at universities everywhere!
          </p>
          <p class="subheading-1">
            Our goal is to make learning more <em>collaborative</em> by encouraging 
            students to share their course notes. That's why we also made a Chrome 
            extension. With the power of Google, you can quickly generate a 
            <a href="https://docs.google.com/document/d/1XlcAy-vrleXBxJl5Qy_SxGUyTqcwdUIhyJI2BygpNEc/edit" target="_blank">
              <strong>notes template</strong>
            </a>
            in Google Docs that's perfect for taking notes with your friends!
          </p>
        </v-col>
        <v-col sm="4">
          <v-img src="/assets/collaborate.svg"></v-img>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
module.exports = {
  components: {
    'search-bar': httpVueLoader('/components/SearchBar.vue')
  }
}
</script>

<style scoped>
a {
  text-decoration: none;
  color: inherit;
}

a:hover {
  color: blue;
}

.no-pad {
  padding: 0;
}

.white-text {
  color: white;
}

.spacer {
  margin: 10px;
}

#starfish-title {
  font-family: 'Caveat Brush'!important;
}

#starfish-subtitle {
  font-family: 'Coming Soon'!important;
}

#starfish-logo {
  height: 1em;
  margin-bottom: -0.1em;
}

#laptop-img {
  max-width: 600px;
}

.wave {
  background: rgb(3,0,57);
  background: linear-gradient(20deg, rgba(3,0,57,1) 0%, rgba(21,101,192,1) 53%, rgba(115,180,255,1) 100%);
  position: relative;
}

.wave:before {
  background: url('/assets/wave.png');
  background-repeat: no-repeat;
  background-size: cover;
  bottom: 0;
  content: '';
  height: 250px;
  left: 0;
  position: absolute;
  width: 100%;  
}

.heading-1 {
  font-size: 1.7rem;
  font-weight: 500;
}

.subheading-1 {
  color: #575757;
  font-size: 1.2rem;
}

#info-section {
  margin: 50px auto;
  max-width: 1200px;
}

@media only screen and (min-height: 850px) {
  .lg-spacer {
    margin: 100px!important;
  }

  .md-spacer {
    margin: 20px!important;
  }
}

@media only screen and (min-width: 1750px) {
  #laptop-img {
    max-width: 800px!important;
  }

  .wave:before {
    height: 425px!important;
  }

  .heading-1 {
    font-size: 2rem;
  }

  .subheading-1 {
    font-size: 1.5rem;
  }
}
</style>
