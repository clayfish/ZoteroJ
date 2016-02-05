package edu.tamu.tcat.zotero;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import edu.tamu.tcat.zotero.basic.v3.search.TagFilterFactory;
import edu.tamu.tcat.zotero.search.TagFilter;
import edu.tamu.tcat.zotero.search.TagFilter.DisjunctiveTagFilter;

public class TagFilterFactoryTests
{

   @Test
   public void testSimpleTag()
   {
      DisjunctiveTagFilter filter = TagFilterFactory.filter("foo");
      assertEquals("tag=foo", filter.buildParameters());
   }

   @Test
   public void testTagWithSpace()
   {
      DisjunctiveTagFilter filter = TagFilterFactory.filter("foo bar");
      assertEquals("tag=foo+bar", filter.buildParameters());
   }

   @Test
   public void testNotTag()
   {
      TagFilter filter = TagFilterFactory.not("foo");
      assertEquals("tag=-foo", filter.buildParameters());
   }

   @Test
   public void testLeadingHypen()
   {
      TagFilter filter = TagFilterFactory.filter("-foo");
      assertEquals("tag=%5C-foo", filter.buildParameters());
   }

   @Test
   public void testSimpleOr()
   {
      DisjunctiveTagFilter filter = TagFilterFactory.filter("foo bar").or("bar");
      assertEquals("tag=foo+bar || bar", filter.buildParameters());
   }

   @Test
   public void testSimpleAnd()
   {
      TagFilter filter = TagFilterFactory.filter("foo").and("bar");
      assertEquals("tag=foo&tag=bar", filter.buildParameters());
   }

   @Test
   public void testConjunctionOfDisjunction()
   {
      DisjunctiveTagFilter fooBar = TagFilterFactory.filter("foo").or("bar");
      DisjunctiveTagFilter upDown = TagFilterFactory.filter("up").or("down");

      assertEquals("tag=foo || bar&tag=up || down", fooBar.and(upDown).buildParameters());
   }
}
