/* LanguageTool, a natural language style checker
 * Copyright (C) 2023 Jaume Ortolà
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
package org.languagetool.rules.spelling.multitoken;


import org.languagetool.*;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.PatternRule;
import org.languagetool.rules.patterns.RuleFilter;
import org.languagetool.rules.spelling.SpellingCheckRule;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.languagetool.tools.StringTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MultitokenSpellerFilter extends RuleFilter {

   /* Provide suggestions for misspelled multitoken expressions, usually proper nouns*/

  @Override
  public RuleMatch acceptRuleMatch(RuleMatch match, Map<String, String> arguments, int patternTokenPos,
                                   AnalyzedTokenReadings[] patternTokens) throws IOException {
    if (Arrays.stream(patternTokens).allMatch(x -> x.isIgnoredBySpeller())) {
      return null;
    }
    String underlinedError = match.getOriginalErrorStr();
    Language lang = ((PatternRule) match.getRule()).getLanguage();
    // check the spelling for some languages in a different way
    boolean areTokensAcceptedBySpeller = false;
    if (lang.getShortCode().equals("en") || lang.getShortCode().equals("de") || lang.getShortCode().equals("pt")) {
      if (lang.getShortCodeWithCountryAndVariant().length()==2) {
        // needed in testing
        lang = lang.getDefaultLanguageVariant();
      }
      JLanguageTool lt = lang.createDefaultJLanguageTool();
      AnalyzedSentence sentence = lt.getRawAnalyzedSentence(underlinedError);
      RuleMatch[] matches = lang.getDefaultSpellingRule().match(sentence);
      if (matches.length == 0) {
        areTokensAcceptedBySpeller = true;
      }
    }

    List<String> replacements = lang.getMultitokenSpeller().getSuggestions(underlinedError, areTokensAcceptedBySpeller);
    if (replacements.isEmpty()) {
      return null;
    }
    if (patternTokenPos==1) {
      List<String> capitalizedReplacements = new ArrayList<>();
      for (String replacement : replacements) {
        if (replacement.equals(replacement.toLowerCase())) {
          String capitalized = StringTools.uppercaseFirstChar(replacement);
          capitalizedReplacements.add(capitalized);
        } else {
          //do not capitalize iPad
          capitalizedReplacements.add(replacement);
        }
      }
      replacements = capitalizedReplacements;
    }
    match.setSuggestedReplacements(replacements);
    return match;
  }

}
