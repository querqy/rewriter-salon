package querqy;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import querqy.model.BooleanClause;
import querqy.model.BooleanQuery;
import querqy.model.BoostQuery;
import querqy.model.Clause.Occur;
import querqy.model.DisjunctionMaxClause;
import querqy.model.DisjunctionMaxQuery;
import querqy.model.Node;
import querqy.model.SubQuery;
import querqy.model.Term;

import java.util.Arrays;
import java.util.List;

/**
 * Copied from querqy-core
 *
 * {@link Matcher}s for {@link SubQuery}s.
 */
public class QuerqyMatchers {
    
    
    
   /**
    * Match {@link DisjunctionMaxClause} with {@link Occur#SHOULD}.
    * 
    * @param clauses
    *           Expected clauses.
    */
   @SafeVarargs
   public static DMQMatcher dmq(TypeSafeMatcher<? extends DisjunctionMaxClause>... clauses) {
      return dmq(should(), clauses);
   }

   /**
    * Match {@link DisjunctionMaxClause}.
    * 
    * @param occur
    *           Expected occur.
    * @param clauses
    *           Expected clauses.
    */
   @SafeVarargs
   public static DMQMatcher dmq(OccurMatcher occur, TypeSafeMatcher<? extends DisjunctionMaxClause>... clauses) {
      return new DMQMatcher(occur, clauses);
   }

   /**
    * Match {@link BooleanQuery} with {@link Occur#SHOULD}.
    * 
    * @param clauses
    *           Expected clauses.
    */
   @SafeVarargs
   public static BQMatcher bq(TypeSafeMatcher<? extends BooleanClause>... clauses) {
      return bq(should(), clauses);
   }

   /**
    * Match {@link BooleanQuery}.
    * 
    * @param occur
    *           Expected occur.
    * @param clauses
    *           Expected clauses.
    */
   @SafeVarargs
   public static BQMatcher bq(OccurMatcher occur, TypeSafeMatcher<? extends BooleanClause>... clauses) {
      return new BQMatcher(occur, clauses);
   }

   /**
    * Match term.
    * 
    * @param field
    *           Expected field.
    * @param value
    *           Expected value.
    */
   public static TermMatcher term(String field, String value) {
      return new TermMatcher(field, value);
   }
   
   public static TermMatcher term(String field, String value, Boolean generated) {
       return new TermMatcher(field, value, generated);
    }

   /**
    * Just match term value.
    * 
    * @param value
    *           Expected value.
    */
   public static TermMatcher term(String value) {
      return new TermMatcher(null, value);
   }
   
   public static TermMatcher term(String value, Boolean generated) {
       return new TermMatcher(null, value, generated);
    }
   
   /**
    * Match a boost query.
    * 
    * @param queryMatcher The expected query matcher
    * @param boost The expected boost factor
    * 
    * @return
    */
   public static BoostQueryMatcher boostQ(TypeSafeMatcher<? extends Node> queryMatcher, float boost) {
       return new BoostQueryMatcher(queryMatcher, boost);
   }

   /**
    * Expected {@link Occur#MUST}.
    */
   public static OccurMatcher must() {
      return new OccurMatcher(Occur.MUST);
   }

   /**
    * Expected {@link Occur#MUST_NOT}.
    */
   public static final OccurMatcher mustNot() {
      return new OccurMatcher(Occur.MUST_NOT);
   }

   /**
    * Expected {@link Occur#SHOULD}.
    */
   public static final OccurMatcher should() {
      return new OccurMatcher(Occur.SHOULD);
   }

   //
   // Inner classes for matchers.
   //

   /**
    * {@link Matcher} for {@link DisjunctionMaxQuery}s.
    */
   private static class DMQMatcher extends SubQueryMatcher<DisjunctionMaxQuery> {
      public DMQMatcher(OccurMatcher occur, TypeSafeMatcher<? extends DisjunctionMaxClause>[] clauses) {
         super(occur, clauses);
      }

      @Override
      public void describeTo(Description description) {
         description.appendText("DMQ:\n ");
         super.describeTo(description);
      }
   }

   /**
    * {@link Matcher} for {@link BooleanQuery}s.
    */
   private static class BQMatcher extends SubQueryMatcher<BooleanQuery> {
      public BQMatcher(OccurMatcher occur, TypeSafeMatcher<? extends BooleanClause>[] clauses) {
         super(occur, clauses);
      }

      @Override
      public void describeTo(Description description) {
         description.appendText("BQ:\n ");
         description.appendText("(");
         super.describeTo(description);
         description.appendText(")\n");
      }
   }

   /**
    * {@link Matcher} for {@link Term}s.
    */
   private static class TermMatcher extends TypeSafeMatcher<Term> {
      private final String expectedField;
      private final String expectedValue;
      private final Boolean expectedGenerated;

      public TermMatcher(String field, String value) {
          this(field, value, null);
      }
      
      public TermMatcher(String field, String value, Boolean expectedGenerated) {
         this.expectedField = field;
         this.expectedValue = value;
         this.expectedGenerated = expectedGenerated;
      }

      @Override
      public void describeTo(Description description) {
         description.appendText("term: " + expectedField + ":" + expectedValue + " (generated:" + expectedGenerated + ")");
      }

      @Override
      protected boolean matchesSafely(Term item) {
         return item != null &&
               // expectedField == null -> do not compare field name.
               (expectedField == null || expectedField.equals(item.getField())) &&
               // Using toString() here to avoid incompatibilities between
               // equals() of different char sequences.
               CharSequenceUtil.equals(expectedValue, item.getValue()) &&
              // expectedValue.equals(item.getValue().toString()) &&
               ((expectedGenerated == null) || (expectedGenerated.booleanValue() == item.isGenerated()))
               ;
      }
   }
   
   /**
    * {@link Matcher} for {@link BoostQuery}s.
    */
   private static class BoostQueryMatcher extends TypeSafeMatcher<BoostQuery> {
       
       final TypeSafeMatcher<? extends Node> queryMatcher;
       final float boost;
       
       public BoostQueryMatcher(TypeSafeMatcher<? extends Node> queryMatcher, float boost) {
           this.queryMatcher = queryMatcher;
           this.boost = boost;
       }

      @Override
      public void describeTo(Description description) {
         description.appendText("BoostQuery:\n ");
         description.appendText("(");
         queryMatcher.describeTo(description);
         description.appendText(", boost: ");
         description.appendValue(boost);
         description.appendText(")\n");
      }

    @Override
    protected boolean matchesSafely(BoostQuery item) {
        return queryMatcher.matches(item.getQuery()) && item.getBoost() == boost;
    }
   }


   /**
    * {@link Matcher} for {@link Occur}s.
    */
   private static class OccurMatcher extends TypeSafeMatcher<Occur> {
      private final Occur expected;

      public OccurMatcher(Occur expected) {
         this.expected = expected;
      }

      @Override
      public void describeTo(Description description) {
         description.appendText("occur: " + expected.name());
      }

      @Override
      protected boolean matchesSafely(Occur occur) {
         return expected.equals(occur);
      }
   }

   /**
    * {@link Matcher} for {@link SubQuery}s.
    */
   private static abstract class SubQueryMatcher<T extends SubQuery<?, ?>> extends TypeSafeMatcher<T> {
      private final OccurMatcher occur;
      private final TypeSafeMatcher<? extends Node>[] clauses;

      public SubQueryMatcher(OccurMatcher occur, TypeSafeMatcher<? extends Node>[] clauses) {
         this.occur = occur;
         this.clauses = clauses;
      }

      @Override
      public void describeTo(Description description) {
         occur.describeTo(description);
         description.appendList("clauses:[", ",\n", "]", Arrays.asList(clauses));
      }

      @Override
      protected boolean matchesSafely(T item) {
         if (!occur.matches(item.occur)) {
            return false;
         }

         List<? extends Node> itemClauses = item.getClauses();
         if (itemClauses == null || itemClauses.size() != clauses.length) {
            return false;
         }

         for (int i = 0; i < clauses.length; i++) {
            if (!clauses[i].matches(itemClauses.get(i))) {
               return false;
            }
         }

         return true;
      }
   }
}
