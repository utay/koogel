<template>
  <div>
    <input v-model="query" class="search" autofocus>
    <button class="search-button" @click="searchWithButton">Hopla</button>
    <div class="results">
      <div class="lds-circle" v-if="isLoading">
        <div></div>
      </div>

      <div v-if="!isLoading">
        <p class="nb-results">{{ totalResults }} results in {{ time }} seconds</p>
        <div v-for="result in results" :key="result.id" class="result">
          <a :href="result.URL" class="url">{{ result.URL }}</a>
          <p v-html="highlight(result)" class="resume"></p>
        </div>
      </div>

      <div v-if="results.length !== 0" class="pages">
        <div v-for="p in pages()" :key="p" @click="setPage(p + 1)">
          <span v-if="page === p + 1" class="page-active">{{ p + 1 }}</span>
          <span v-else class="page">{{ p + 1 }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

String.prototype.splice = function(idx, rem, str) {
  return this.slice(0, idx) + str + this.slice(idx + Math.abs(rem));
};

export default {
  name: "Home",

  mounted() {
    document.querySelector(".search").addEventListener("keypress", e => {
      const key = e.which || e.keyCode;
      if (key === 13) {
        this.searchWithButton();
      }
    });
  },

  data() {
    return {
      query: "",
      time: 0,
      totalResults: 0,
      isLoading: false,
      results: [],
      page: 1,
      resultsPerPage: 10,
      limit: 10
    };
  },

  watch: {
    page(oldVal, val) {
      this.search();
    }
  },

  methods: {
    pages() {
      return [
        ...Array(Math.round(this.totalResults / this.resultsPerPage + 1)).keys()
      ];
    },

    isLetter(str) {
      return str.length === 1 && str.match(/[a-z]/i);
    },

    cropToSentence(str, firstHighlightIndex) {
      if (firstHighlightIndex == -1) {
        return str;
      }

      let capitalLetterIndex = 0;
      for (let i = firstHighlightIndex; i > 0; --i) {
        if (this.isLetter(str[i]) && str[i] === str[i].toUpperCase()) {
          capitalLetterIndex = i;
          break;
        }
      }

      return str.substring(capitalLetterIndex);
    },

    highlight(result) {
      let res = result.rawContent;
      let i = 0;
      let firstHighlightIndex = -1;

      for (const term in result.metadata) {
        for (const index of result.metadata[term].rawIndices) {
          const highlight = [index, term.length];
          if (firstHighlightIndex === -1) firstHighlightIndex = index;
          const start = Math.max(highlight[0] - 1 + i, 0);
          res = res.splice(start, 0, "<b>");
          i += 3;
          const end = Math.max(highlight[0] - 1 + i, 0) + 1 + highlight[1];
          res = res.splice(end, 0, "</b>");
          i += 4;
        }
      }

      return this.cropToSentence(res, firstHighlightIndex);
    },

    searchWithButton() {
      this.page = 1;
      this.search();
    },

    async search() {
      this.isLoading = true;

      const start = new Date().getTime();
      const response = await axios.get(
        `http://localhost:12000/search?q=${this.query}&limit=${
          this.limit
        }&offset=${(this.page - 1) * this.resultsPerPage}`
      );
      const end = new Date().getTime();

      // const response = {
      //   data: {
      //     query: "insideapp",
      //     documents: [
      //       {
      //         URL: "https://insideapp.io",
      //         metadata: {
      //           facebook: {
      //             rawIndices: [4250]
      //           }
      //         },
      //         rawContent: ""
      //       }
      //     ],
      //     totalResults: 1
      //   }
      // };

      this.results = response.data.documents;
      this.totalResults = response.data.totalResults;
      this.time = (end - start) / 1000;
      this.isLoading = false;
    },

    setPage(page) {
      this.page = page;
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

.search-button {
  margin-left: 20px;
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

.resume {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.page {
  cursor: pointer;
  color: #1a0dab;
  margin-right: 5px;
}

.page:hover {
  text-decoration: underline;
}

.page-active {
  margin-right: 5px;
  color: black;
}

.page-active:hover {
  text-decoration: none;
}

.pages {
  display: flex;
  justify-content: center;
}

.lds-circle {
  display: inline-block;
  transform: translateZ(1px);
}

.lds-circle > div {
  display: inline-block;
  width: 51px;
  height: 51px;
  margin: 6px;
  border-radius: 50%;
  background: #d8d8d8;
  animation: lds-circle 2.4s cubic-bezier(0, 0.2, 0.8, 1) infinite;
}

@keyframes lds-circle {
  0%,
  100% {
    animation-timing-function: cubic-bezier(0.5, 0, 1, 0.5);
  }
  0% {
    transform: rotateY(0deg);
  }
  50% {
    transform: rotateY(1800deg);
    animation-timing-function: cubic-bezier(0, 0.5, 0.5, 1);
  }
  100% {
    transform: rotateY(3600deg);
  }
}
</style>
