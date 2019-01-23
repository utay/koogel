<template>
  <div>
    <input v-model="query" class="search" autofocus>
    <div class="results">
      <p class="nb-results">{{ results.length }} results in {{ time }} seconds</p>
      <div v-for="result in results" :key="result.id" class="result">
        <a :href="result.url" class="url">{{ result.url }}</a>
        <p v-html="highlight(result)"></p>
      </div>
    </div>
  </div>
</template>

<script>
String.prototype.splice = function(idx, rem, str) {
  return this.slice(0, idx) + str + this.slice(idx + Math.abs(rem));
};

export default {
  name: "Home",

  data() {
    return {
      query: "",
      time: 0,
      isLoading: false,
      results: Array.apply(null, { length: 10 }).map(Function.call, () => ({
        id: Math.random(),
        url: "http://wikipedia.org",
        resume:
          "test ɛst masculin ... elles prennent ceux de test, de coquille ou d'écaille, selon leur plus ou moins de consistance. ... prendre l'empreinte du test disparu.",
        highlights: [[10, 8], [23, 5]]
      }))
    };
  },

  watch: {
    query: async function(val, oldVal) {
      this.isLoading = true;

      const start = new Date().getTime();
      let j = 0;
      for (let i = 0; i < 500000; ++i) {
        j++;
      }
      const end = new Date().getTime();

      this.time = (end - start) / 1000;
      this.isLoading = false;
    }
  },

  methods: {
    highlight(result) {
      if (result.highlights.length == 0) {
        return result.resume;
      }

      let res = result.resume;

      let i = 0;

      for (const highlight of result.highlights) {
        res = res.splice(highlight[0] - 1 + i, 0, "<b>");
        i += 3;
        res = res.splice(highlight[0] - 1 + highlight[1] + i, 0, "</b>");
        i += 4;
      }

      return res;
    }
  }
};
</script>

<style scoped>
.search {
  padding-left: 20px;
  border-color: white;
  border-radius: 30px;
  width: 40%;
  height: 40px;
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19);
  font-size: 22px;
  margin-bottom: 15px;
}

input:focus {
  outline: 0;
}

.results {
  text-align: left;
  margin-left: 20%;
  width: 50%;
}

.nb-results {
  color: gray;
  font-size: 12px;
}

.url {
  color: #1a0dab;
  font-size: 20px;
  text-decoration: none;
}

.url:hover {
  text-decoration: underline;
}

.result {
  margin-bottom: 25px;
}

.result p {
  margin-top: 8px;
}
</style>
