package edu.tamu.tcat.zotero.basic.v3.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.ws.rs.client.WebTarget;

import edu.tamu.tcat.zotero.search.TagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.ConjunctiveTagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.DisjunctiveTagFilter;

public abstract class TagFilterFactory
{
   public interface WebTargetTagFilter extends TagFilter
   {
      WebTarget apply(WebTarget target);
   }

   public static String encode(String tag)
   {
      try
      {
         return URLEncoder.encode(tag, "UTF-8");
      }
      catch (UnsupportedEncodingException e)
      {
         throw new IllegalStateException("Cannot find UTF-8 charset.", e);
      }
   }

   public static DisjunctiveTagFilter filter(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      return new OrTagClause(tag);
   }

   public static ConjunctiveTagFilter and(String... tags)
   {
      TagFilter[] filters = (TagFilter[])Arrays.stream(tags).map(OrTagClause::new).toArray();
      return new AndTagClause(filters);
   }

   public static DisjunctiveTagFilter or(String... tags)
   {
      return new OrTagClause(tags);
   }

   public static ConjunctiveTagFilter not(String tag)
   {
      if (tag.startsWith("-"))
         tag = "\\" + tag;

      OrTagClause clause = new OrTagClause("-" + tag);
      return new AndTagClause(clause);
   }
}
