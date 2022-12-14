import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
  String dataSetPath;

  public static class Movie {
    String posterLink;
    String seriesTitle;
    String releasedYear;
    String certificate;
    String runtime;
    String genre;
    String imdbRating;
    String overView;
    String metaScore;
    String director;
    String star1, star2, star3, star4;
    String noOfVotes;
    String gross;
    List<String> star = new ArrayList<>();

    public Movie(String _Poster_Link, String _Series_Title,
                 String _Released_Year, String _Certificate, String _Runtime,
                 String _Genre, String _IMDB_Rating, String _Overview, String _Meta_score,
                 String _Director, String _Star1,
                 String _Star2, String _Star3, String _Star4,
                 String _No_of_Votes, String _Gross) {
      posterLink = _Poster_Link;
      seriesTitle = _Series_Title;
      releasedYear = _Released_Year;
      certificate = _Certificate;
      runtime = _Runtime;
      genre = _Genre;
      imdbRating = _IMDB_Rating;
      metaScore = _Meta_score;
      director = _Director;
      star1 = _Star1;
      star2 = _Star2;
      star3 = _Star3;
      star4 = _Star4;
      noOfVotes = _No_of_Votes;
      gross = _Gross;
      star.add(star1);
      star.add(star2);
      star.add(star3);
      star.add(star4);
      String tmp = "";
      for (int i = 0; i < _Series_Title.length(); ++i) {
        if (_Series_Title.charAt(i) != '"') {
          tmp += _Series_Title.charAt(i);
        }
      }
      seriesTitle = tmp;
      tmp = "";

      for (int i = 0; i < _Overview.length(); ++i) {
        if (i == 0 || i == _Overview.length() - 1) {
          if (_Overview.charAt(i) != '"') {
            tmp += _Overview.charAt(i);
          }
          else {
            tmp += _Overview.charAt(i);
          }
        }
      }
      overView = tmp;
    }

    public int getReleasedYear() {
      return Integer.parseInt(releasedYear);
    }

    public String getGenre() {
      return genre;
    }

    public List<String> getStar() {
      return star;
    }

    public int getOverview() {
      return overView.length();
    }

    public String getSeriesTitle() {
      return seriesTitle;
    }

    public int getRuntime() {
      int num = 0;
      for (int i = 0; i < runtime.length(); ++i) {
        if (!(runtime.charAt(i) >= '0' && runtime.charAt(i) <= '9')) {
          break;
        }
        num = 10 * num + (runtime.charAt(i) - '0');
      }
      return num;
    }

    public float getRating() {
      return Float.parseFloat(imdbRating);
    }

    public float getGross() {
      if (gross.equals("")) {
        return 0.0f;
      }
      String tmp = "";
      for (int i = 0; i < gross.length(); ++i) {
        if (gross.charAt(i) != ',' && gross.charAt(i) != '"') {
          tmp += gross.charAt(i);
        }
      }
      return Float.parseFloat(tmp);
    }
  }

  public Stream<Movie> getStream(String dataset_path) throws IOException {
    return Files.lines(Paths.get(dataset_path).toAbsolutePath())
              .map(l -> l.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1))
              .skip(1)
              .map(a -> new Movie(a[0], a[1], a[2], a[3], a[4], a[5], a[6], a[7], a[8], a[9], a[10], a[11], a[12],
                      a[13], a[14], a[15]))
              ;
  }

  public MovieAnalyzer(String dataset_path) {
    dataSetPath = dataset_path;
  }

  public Map<Integer, Integer> getMovieCountByYear() throws IOException {
    Stream<Movie> movieAnalyzer = getStream(dataSetPath);
    Map<Integer, Long> movies = movieAnalyzer.filter(t -> !Objects.equals(t.releasedYear, ""))
            .collect(Collectors.groupingBy(Movie::getReleasedYear, Collectors.counting()));
    Map<Integer, Integer> s = new HashMap<>();
    for (Map.Entry<Integer, Long> entry : movies.entrySet()) {
            //System.out.println(entry.getKey()+" "+entry.getValue());
      if (s.containsKey(entry.getKey())) {
        int value = s.get(entry.getKey());
        value += entry.getValue().intValue();
        s.put(entry.getKey(), value);
      } else {
        s.put(entry.getKey(), entry.getValue().intValue());
      }
    }
    List<Map.Entry<Integer, Integer>> save = new ArrayList<>(s.entrySet());
    Collections.sort(save, new Comparator<Map.Entry<Integer, Integer>>() {
        @Override
        public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
            int compare = (o1.getKey()).compareTo(o2.getKey());
            return -compare;
        }
    });
    Map<Integer, Integer> ans = new LinkedHashMap<>();
    for (Map.Entry<Integer, Integer> entry : save) {
      ans.put(entry.getKey(), entry.getValue());
    }
    return ans;
  }

  public Map<String, Integer> getMovieCountByGenre() throws IOException {
    Stream<Movie> movieAnalyzer = getStream(dataSetPath);
    Map<String, Long> movies = movieAnalyzer.filter(t -> !Objects.equals(t.genre, ""))
            .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
    Map<String, Integer> save = new TreeMap<String, Integer>();
    Map<String, Integer> result = new LinkedHashMap<String, Integer>();
    for (Map.Entry<String, Long> entry : movies.entrySet()) {
      String name = entry.getKey();
      if (name.charAt(0) == '"') {
        String now = "";
        for (int i = 1; i < name.length(); ++i) {
          if (name.charAt(i) == ' ') {
            continue;
          }
          if (name.charAt(i) == '"') {
            continue;
          }
          if (name.charAt(i) != ',') {
            now += name.charAt(i);
          }
          else {
            if (save.containsKey(now)) {
              int value = save.get(now);
              value += entry.getValue().intValue();
              save.put(now, value);
            } else {
              save.put(now, entry.getValue().intValue());
            }
            now = "";
          }
        }
        if (!now.equals("")) {
          if (save.containsKey(now)) {
            int value = save.get(now);
            value += entry.getValue().intValue();
            save.put(now, value);
          } else {
            save.put(now, entry.getValue().intValue());
          }
        }
      } else {
        String now = entry.getKey();
        if (save.containsKey(now)) {
          int value = save.get(now);
          value += entry.getValue().intValue();
          save.put(now, value);
        } else {
          save.put(entry.getKey(), entry.getValue().intValue());
        }
      }
    }
    Map<String, Integer> ans = new TreeMap<String, Integer>(save);
    List<Map.Entry<String, Integer>> list = new ArrayList<>(ans.entrySet());
    Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            int compare = (o1.getValue()).compareTo(o2.getValue());
            if (compare == 0) {
              compare = o2.getKey().compareTo(o1.getKey());
            }
            return -compare;
        }
    });
    for (Map.Entry<String, Integer> item : list) {
      result.put(item.getKey(), item.getValue());
    }
    return result;
  }

  public Map<List<String>, Integer> getCoStarCount() throws IOException {
    Stream<Movie> movieAnalyzer = getStream(dataSetPath);
    Map<List<String>, Long> movies = movieAnalyzer.filter(t -> t.star.size() != 0)
                .collect(Collectors.groupingBy(Movie::getStar, Collectors.counting()));
    Map<List<String>, Integer> save = new HashMap<>();
    for (Map.Entry<List<String>, Long> item : movies.entrySet()) {
      List<String> now = item.getKey();
      for (int i = 0; i < now.size(); ++i) {
        for (int j = i + 1; j < now.size(); ++j) {
          List<String> f = new ArrayList<>();
          String star1 = now.get(i);
          String star2 = now.get(j);
          if (star1.compareTo(star2) > 0) {
            String tmp = star1;
            star1 = star2;
            star2 = tmp;
          }
          f.add(star1);
          f.add(star2);
          if (save.containsKey(f)) {
            int value = save.get(f);
            value += item.getValue().intValue();
            save.put(f, value);
          } else {
            save.put(f, item.getValue().intValue());
          }
        }
      }
    }
    return save;
  }

  public List<String> getTopMovies(int top_k, String by) throws IOException {
    List<String> ans = new ArrayList<>();
    if (by.equals("runtime")) {
      Stream<Movie> movieAnalyzer = getStream(dataSetPath);
      List<Movie> save = movieAnalyzer
                  .filter(t -> !Objects.equals(t.runtime, ""))
                  .sorted(Comparator.comparing(Movie::getRuntime).reversed().thenComparing(Movie::getSeriesTitle)).collect(Collectors.toList());
      for (int i = 0; i < top_k; ++i) {
        ans.add(save.get(i).getSeriesTitle());
      }
      return ans;
    } else {
      Stream<Movie> movieAnalyzer = getStream(dataSetPath);
      List<Movie> save = movieAnalyzer
              .filter(t -> !Objects.equals(t.overView, ""))
              .sorted(Comparator.comparing(Movie::getOverview).reversed().thenComparing(Movie::getSeriesTitle)).collect(Collectors.toList());
      for (int i = 0; i < top_k; ++i) {
        ans.add(save.get(i).getSeriesTitle());
      }
      return ans;
    }
  }

  public List<String> getTopStars(int top_k, String by) throws IOException {
    List<String> ans = new ArrayList<>();
    if (by.equals("rating")) {
      Stream<Movie> movieAnalyzer = getStream(dataSetPath);
      Map<String, Double> rating = new HashMap<>();
      Map<String, Double> movie = new HashMap<>();
      Map<String, Double> avg = new HashMap<>();
      List<Movie> save = movieAnalyzer.collect(Collectors.toList());
      for (int i = 0; i < save.size(); ++i) {
        List<String> star = save.get(i).getStar();
        for (int j = 0; j < star.size(); ++j) {
          if (!movie.containsKey(star.get(j))) {
            movie.put(star.get(j), 1.0);
          } else {
            Double value = movie.get(star.get(j));
            value += 1.0;
            movie.put(star.get(j), value);
          }
          if (!rating.containsKey(star.get(j))) {
            rating.put(star.get(j), (double) save.get(i).getRating());
          } else {
            double value = rating.get(star.get(j));
            value += save.get(i).getRating();
            rating.put(star.get(j), value);
          }
        }
      }
      for (int i = 0; i < save.size(); ++i) {
        List<String> star = save.get(i).getStar();
        for (int j = 0; j < star.size(); ++j) {
          if (!avg.containsKey(star.get(j))) {
            avg.put(star.get(j), (double) (rating.get(star.get(j)) / movie.get(star.get(j))));
          }
        }
      }
      List<Map.Entry<String, Double>> list = new ArrayList<>(avg.entrySet());
      Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
          @Override
          public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            int compare = (o1.getValue()).compareTo((o2.getValue()));
            if (compare == 0) {
              compare = o2.getKey().compareTo(o1.getKey());
            }
            return -compare;
          }
      });
      for (int i = 0; i < top_k; ++i) {
        ans.add(list.get(i).getKey());
      }
      return ans;
    } else {
      Stream<Movie> movieAnalyzer = getStream(dataSetPath);
      Map<String, Float> rating = new HashMap<>();
      Map<String, Float> movie = new HashMap<>();
      Map<String, Double> avg = new HashMap<>();
      List<Movie> save = movieAnalyzer.collect(Collectors.toList());
      for (int i = 0; i < save.size(); ++i) {
        List<String> star = save.get(i).getStar();
        for (int j = 0; j < star.size(); ++j) {
          if (!movie.containsKey(star.get(j)) && save.get(i).getGross() != 0.0) {
            movie.put(star.get(j), 1f);
          } else {
            if (save.get(i).getGross() == 0.0) {
              continue;
            }
            float value = movie.get(star.get(j));
            value += 1.0;
            movie.put(star.get(j), value);
          }
          if (!rating.containsKey(star.get(j))) {
            rating.put(star.get(j), save.get(i).getGross());
          } else {
            float value = rating.get(star.get(j));
            value += save.get(i).getGross();
            rating.put(star.get(j), value);
          }
        }
      }
      for (int i = 0; i < save.size(); ++i) {
        List<String> star = save.get(i).getStar();
        for (int j = 0; j < star.size(); ++j) {
          if (!avg.containsKey(star.get(j)) && movie.get(star.get(j)) != null) {
            avg.put(star.get(j), (double) (rating.get(star.get(j)) / movie.get(star.get(j))));
          }
        }
      }
      List<Map.Entry<String, Double>> list = new ArrayList<>(avg.entrySet());
      Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
          @Override
          public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
            int compare = (o1.getValue()).compareTo(o2.getValue());
            if (compare == 0) {
              compare = o2.getKey().compareTo(o1.getKey());
            }
            return -compare;
          }
      });
      for (int i = 0; i < top_k; ++i) {
        ans.add(list.get(i).getKey());
      }
      return ans;
    }
  }

  public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
    Stream<Movie> movieAnalyzer = getStream(dataSetPath);
    List<Movie> save = movieAnalyzer.collect(Collectors.toList());
    List<String> ans = new ArrayList<>();
    for (int j = 0; j < save.size(); ++j) {
      String name = save.get(j).getGenre();
      String now = "";
      if (name.charAt(0) != '"') {
        if (name.equals(genre)) {
          if (save.get(j).getRating() >= min_rating && save.get(j).getRuntime() <= max_runtime) {
            ans.add(save.get(j).getSeriesTitle());
            continue;
          }
        }
      }
      for (int i = 1; i < name.length(); ++i) {
        if (name.charAt(i) == ' ') {
          continue;
        }
        if (name.charAt(i) == '"') {
          continue;
        }
        if (name.charAt(i) != ',') {
          now += name.charAt(i);
        }
        else {
          if (now.equals(genre)) {
            if (save.get(j).getRating() >= min_rating && save.get(j).getRuntime() <= max_runtime) {
              ans.add(save.get(j).getSeriesTitle());
            }
          }
          now = "";
        }
      }
      if (!now.equals("")) {
        if (now.equals(genre)) {
          if (save.get(j).getRating() >= min_rating && save.get(j).getRuntime() <= max_runtime) {
            ans.add(save.get(j).getSeriesTitle());
          }
        }
      }
    }
    Collections.sort(ans);
    return ans;
  }
}
