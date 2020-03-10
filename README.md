# Recipe search

For purpose of this exercise I've created different SearchEngines so we can compare each solution easily.
From my previous experience working with recipies, I think that counting occurance of each word is not the best approach in terms of searching recipiesthis kind of texts.
Looking separatly at title and the rest of the recipe (title with high relevancy, content lower) works as expected.
Provided search solution allows us to even search for recipies using list of ingredients (InvertedIndexSearch and RegexSearch) like so `(search-recipe "onion carrot potato")`

We cannot be sure about recipies structure, so for the sake of this exercise I assumed that first line is a recipe title, and the rest is the recipe itself. 

## Usage

1. Start REPL
2. Load `core.clj`
3. Call `init` to load recipe files and to initialize indexes
3. Call `search-recipe`
4. See results as maps

Example calls:
`(search-recipe "carrot soup")`
`(search-recipe "potato soup" 30)`

## Search Engines
We have following search engines available

### String search
Simple string search

- RegexSearch
  Regex search with wildcard example: `"carrout soup" => "(?ism)carrot.*?soup"`
- ExactRegexSearch
  Simple Regex search example: `"carrout soup" => "(?ism)carrot soup"`
- BoyerMooreHoopsterSearch
  Find needle position in haystack using Boyer Moore Hoopster algorithm [link here](https://en.wikipedia.org/wiki/Boyer%E2%80%93Moore%E2%80%93Horspool_algorithm "Boyer Moore Horspool Algorithm")
      
### Indexed search
- InvertedIndexesSearch
  Most efficient way of searching. Breaks down all recipes by words and creates inverted index.
  Search is done using matching on sets [link here](https://en.wikipedia.org/wiki/Inverted_index "Inverted Index")


## TODO
Future work could include

- Refactor
- More tests
- Seek feedback
- For inverted engine, we could make sure that every word is singular, so it will provide even better search results
- Docker file and http interface to use as a service
- Extract ingredients from recipes
- autoload new recipies - dirwatch

In real world we could use Lucene or Elastic/ElasticSearch

## Tests
Few simple tests in `/test`

Acceptance criteria test is in `core_test.clj` - long one, build inverted index

## Benchmark
Simple banchmark using `time` function

1. Start REPL
2. Load `core.clj`
3. Call `init` to load recipe files and to initialize indexes
3. Call `test-search-recipe`
4. See compared results

Example calls:
`(test-search-recipe "carrot")`
`(test-search-recipe "potato soup" 30)`
