package edu.tamu.tcat.zotero.basic.v3.search;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import edu.tamu.tcat.zotero.basic.v3.search.TagFilterFactory.WebTargetTagFilter;
import edu.tamu.tcat.zotero.search.TagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.ConjunctiveTagFilter;

public class AndTagClause implements ConjunctiveTagFilter, WebTargetTagFilter
{
   private final List<TagFilter> clauses;

   public AndTagClause(TagFilter... clauses)
   {
      this.clauses = Arrays.asList(clauses);
   }

   @Override
   public String getValue()
   {
      List<String> params = clauses.stream().map(f -> f.buildParameters()).collect(toList());
      return String.join("&", params);
   }

   @Override
   public AndTagClause exclude(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      return and(TagFilterFactory.not(tag));
   }

   @Override
   public AndTagClause and(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      return and(new OrTagClause(tag));
   }

   @Override
   public AndTagClause and(TagFilter filter)
   {
      ArrayList<TagFilter> copy = new ArrayList<>(clauses);
      if (filter instanceof AndTagClause)
      {
         AndTagClause source = (AndTagClause)filter;
         copy.addAll(source.clauses);
      }
      else
      {
         copy.add(filter);
      }

      return new AndTagClause(clauses.toArray(new TagFilter[copy.size()]));
   }

   @Override
   public String buildParameters()
   {
       List<String> params = clauses.stream().map(f -> f.buildParameters()).collect(toList());
       return String.join("&", params);
   }

   @Override
   public WebTarget apply(WebTarget target)
   {
      List<String> params = clauses.stream().map(f -> f.getValue()).collect(toList());
//      target.queryParam("tag", params);
      return target.queryParam("tag", String.join(" || ", params));
   }
}