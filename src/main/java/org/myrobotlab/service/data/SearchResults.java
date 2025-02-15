package org.myrobotlab.service.data;

import java.util.ArrayList;
import java.util.List;

// FIXME me have a more universal description of SearchResults
// urls ? confidence ?
public class SearchResults {

  public String searchText;
  public String errorText;
  public List<String> text = new ArrayList<>();
  public List<ImageData> images = new ArrayList<>();

  public SearchResults(String searchText) {
    this.searchText = searchText;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String t : text) {
      sb.append(t);
    }
    return sb.toString();
  }

  public String getText() {
    StringBuilder sb = new StringBuilder();
    for (String t : text) {
      sb.append(t);
    }
    return sb.toString();
  }

  public String getTextAndImages() {
    StringBuilder sb = new StringBuilder();
    for (String t : text) {
      sb.append(t);
    }

    // might have to do some encoding at some point in the future
    // arbitrary format
    for (ImageData img : images) {
      sb.append("\n");
      sb.append(img.src);
      sb.append("\n");
    }
    return sb.toString();
  }
}
