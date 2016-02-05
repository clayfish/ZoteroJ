package edu.tamu.tcat.zotero.search;

/**
 * Defines a filter to restrict searches based on user-defined tags. Instances must be
 * immutable. Tag filters are composed in conjunctive normal form
 * (https://en.wikipedia.org/wiki/Conjunctive_normal_form), that is, AND of ORs.
 *
 * <p>
 * For a documentation on the corresponding Zotero REST API definition, see
 * {@link https://www.zotero.org/support/dev/web_api/v3/basics#search_syntax}
 */
public interface TagFilter
{
   /**
    * @return The query parameters that embody this tag definition as defined by the Zotero
    *       REST API. The tag values will have been URL encoded.
    */
   String buildParameters();

   /**
    * @return A string representation of the value associated with this tag filter. For the
    *       {@link DisjunctiveTagFilter}, this must return a value that can be used to set
    *       value of the <code>tag</code> query parameter using a programmatic API.
    * @apiNote This is intended for use by implementations.
    */
   String getValue();

   /**
    *  An OR filter clause. This filter will match items that have either a single tag or any
    *  one of multiple tags.
    *
    *  <p>
    *  Currently, the use of multiple not clauses within a single {@code DisjunctiveTagFilter}
    *  is not supported (that is, not A or not B).
    *
    *  Matches items having any one of a set of tags (tags A or B).
    *  Each action should return a new instance of the filter.
    *
    */
   public static interface DisjunctiveTagFilter extends TagFilter
   {
      /**
       * Creates a new filter that expands the set of items matched to include those items that
       * have the supplied tag.
       *
       * @param tag The tag to match.
       * @return A new {@code DisjunctiveTagFilter} with the added clause.
       */
      DisjunctiveTagFilter or(String tag);

      // TODO add DisjunctiveTagFilter orNot(String tag);
      /**
       * Creates a {@link ConjunctiveTagFilter} that restricts the scope of results to those
       * items that match this filter and do not have the supplied tag.
       *
       * @param tag The tag to be excluded.
       * @return A {@code ConjunctiveTagFilter} with the added restriction.
       */
      ConjunctiveTagFilter exclude(String tag);

      /**
       * Creates a {@link ConjunctiveTagFilter} that restricts the scope of results to those
       * items that match this filter and and also have the supplied tag.
       *
       * @param tag The tag to be matched.
       * @return A {@code ConjunctiveTagFilter} with the added restriction.
       */
      ConjunctiveTagFilter and(String tag);

      /**
       * Creates a {@link ConjunctiveTagFilter} that restricts the scope of results to those
       * items that match this filter and the supplied filter.
       *
       * @param filter The filter to be added to this one.
       * @return A {@code ConjunctiveTagFilter} with the added restriction.
       */
      ConjunctiveTagFilter and(TagFilter filter);
   }

   /**
    * An AND filter clause. This represents the union of two or more {@link DisjunctiveTagFilter}s
    * that returns items that match ALL of the component clauses.
    */
   public static interface ConjunctiveTagFilter extends TagFilter
   {
      /**
       * Adds a clause to require that only items have that have the supplied tag will
       * match the filter.
       *
       * @param tag The tag to match.
       * @return A new {@code ConjunctiveTagFitler} with the added clause.
       */
      ConjunctiveTagFilter and(String tag);

      /**
       * Adds a clause to require that only items that match the supplied filter have will
       * match the returned filter.
       *
       * @param filter The class to add to the filter.
       * @return A new {@code ConjunctiveTagFitler} with the added clause.
       */
      ConjunctiveTagFilter and(TagFilter filter);

      /**
       * Adds a clause that requires that only items that do not have the supplied tag will
       * match the returned filter.
       *
       * @param tag The tag to be excluded
       * @return A new {@code ConjunctiveTagFitler} with the added clause.
       */
      ConjunctiveTagFilter exclude(String tag);
   }

}
