package edu.tamu.tcat.zotero.basic.v3.search;

import static java.util.stream.Collectors.toList;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.WebTarget;

import edu.tamu.tcat.zotero.basic.v3.search.TagFilterFactory.WebTargetTagFilter;
import edu.tamu.tcat.zotero.search.TagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.DisjunctiveTagFilter;

class OrTagClause implements DisjunctiveTagFilter, WebTargetTagFilter
{
   private final List<String> tags;

   OrTagClause(String... tags)
   {
      this.tags = Arrays.asList(tags);
   }

   @Override
   public OrTagClause or(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      List<String> copy = new ArrayList<>(tags);
      copy.add(tag);

      return new OrTagClause(copy.toArray(new String[copy.size()]));
   }

   @Override
   public String getValue()
   {
      List<String> encoded = tags.stream().map(TagFilterFactory::encode).collect(toList());
      return String.join(" || ", encoded);
   }

   @Override
   public AndTagClause exclude(String tag)
   {
      return new AndTagClause(this, TagFilterFactory.not(tag));
   }

   @Override
   public AndTagClause and(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      return new AndTagClause(this, new OrTagClause(tag));
   }

   @Override
   public AndTagClause and(TagFilter filter)
   {
      return (filter instanceof AndTagClause)
            ? ((AndTagClause)filter).and(this)  // ensure that AndTagClauses are properly decomposed into disjunctions
            : new AndTagClause(this, filter);
   }

   @Override
   public String buildParameters()
   {
      return MessageFormat.format("tag={0}", getValue());
   }

   @Override
   public WebTarget apply(WebTarget target)
   {
      return target.queryParam("tag", getValue());
   }
}