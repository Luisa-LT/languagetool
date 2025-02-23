/* LanguageTool, a natural language style checker 
 * Copyright (C) 2014 Daniel Naber (http://www.danielnaber.de)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.rules.nl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestTools;
import org.languagetool.language.Dutch;
import org.languagetool.rules.RuleMatch;

public class MorfologikDutchSpellerRuleTest {

  @Test
  public void testSpeller() throws IOException {
    Dutch language = new Dutch();
    MorfologikDutchSpellerRule rule = new MorfologikDutchSpellerRule(TestTools.getEnglishMessages(), language, null);
    JLanguageTool lt = new JLanguageTool(language);

    assertEquals(0, rule.match(lt.getAnalyzedSentence("Amsterdam")).length);
    assertEquals(0, rule.match(lt.getAnalyzedSentence("ipv")).length); // in ignore.txt
    assertEquals(0, rule.match(lt.getAnalyzedSentence("voorzover")).length); // in ignore.txt

    assertEquals(1, rule.match(lt.getAnalyzedSentence("FoobarWrongxx")).length); // camel case is not ignored
    assertEquals(1, rule.match(lt.getAnalyzedSentence("foobarwrong")).length);

    assertEquals(0, rule.match(lt.getAnalyzedSentence("kómen")).length);
    assertEquals(0, rule.match(lt.getAnalyzedSentence("háár")).length);
    assertEquals(0, rule.match(lt.getAnalyzedSentence("kán")).length);
    assertEquals(0, rule.match(lt.getAnalyzedSentence("ín")).length);

    assertEquals(0, rule.match(lt.getAnalyzedSentence("géén")).length);

    assertEquals(0, rule.match(lt.getAnalyzedSentence("déúr")).length);
    assertEquals(1, rule.match(lt.getAnalyzedSentence("déur")).length);
    assertEquals(0, rule.match(lt.getAnalyzedSentence("deur-knop")).length);
    //unknown followed by EN, should be accepted as it's not in EN dict
    assertEquals(1, rule.match(lt.getAnalyzedSentence("Deze duifkuiker was vlak onder de oever aan het jagen.")).length);
    // unknown followed by EN, should get detected as it's in disambig entity
    assertEquals(0, rule.match(lt.getAnalyzedSentence("Ik vond het indubitably preposterous, wat vond jij ervan?")).length);

    RuleMatch[] matches1 = rule.match(lt.getAnalyzedSentence("thailan."));
    assertThat(matches1.length, is(1));
    assertThat(matches1[0].getSuggestedReplacements().get(0), is("Thailand"));
    assertThat(matches1[0].getSuggestedReplacements().size(), is(1));
    assertThat(matches1[0].getFromPos(), is(0));
    assertThat(matches1[0].getToPos(), is(7));

  }
}
