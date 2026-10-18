package random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/** Small dependency-free regression suite executed by the Gradle test task. */
public final class RandomAlgorithmTest{
    private RandomAlgorithmTest(){
    }

    public static void main(String[] args){
        permutationHasNoDuplicatesAndNoOmissions();
        permutationAvoidsFixedPointsWhenPossible();
        cacheKeyAndDigestAreStableAndSensitive();
        textSanitizationPreservesFormatting();
        instructionalTextSelectionIsScopedAndFormatSafe();
        formatArgumentPatternIsAndroidSafe();
        textureFitIsAspectPreserving();
        System.out.println("Random algorithm tests passed.");
    }

    private static void permutationHasNoDuplicatesAndNoOmissions(){
        List<String> ids = new ArrayList<>();
        for(int i = 0; i < 128; i++) ids.add("id-" + i);
        Map<String, String> mapping = RandomPermutationController.mapping(ids, 0x1234L);
        check(RandomStateStore.mappingComplete(ids, mapping), "mapping is not a complete permutation");
        check(RandomPermutationController.avoidsFixedPoints(ids, mapping), "large mapping contains a fixed point");
    }

    private static void permutationAvoidsFixedPointsWhenPossible(){
        for(int size = 1; size <= 8; size++){
            List<String> ids = new ArrayList<>();
            for(int i = 0; i < size; i++) ids.add("v" + i);
            Map<String, String> mapping = RandomPermutationController.mapping(ids, size * 19L);
            check(RandomStateStore.mappingComplete(ids, mapping), "small mapping is incomplete: " + size);
            check(RandomPermutationController.avoidsFixedPoints(ids, mapping), "small mapping has a fixed point: " + size);
        }
        List<String> one = Arrays.asList("only");
        check(RandomPermutationController.avoidsFixedPoints(one, RandomPermutationController.mapping(one, 1L)),
            "one-element mapping should be accepted as a degraded pool");
    }

    private static void cacheKeyAndDigestAreStableAndSensitive(){
        RandomStateStore store = null;
        String first = "algorithm=2|identity=save-a|content=content-a|text=true|texture=false";
        String same = "algorithm=2|identity=save-a|content=content-a|text=true|texture=false";
        String changed = "algorithm=2|identity=save-a|content=content-b|text=true|texture=false";
        check(RandomPermutationController.digest(first).equals(RandomPermutationController.digest(same)), "digest is not stable");
        check(!RandomPermutationController.digest(first).equals(RandomPermutationController.digest(changed)), "content change did not invalidate digest");
        check(store == null, "test guard");
    }

    private static void textSanitizationPreservesFormatting(){
        String value = RandomTextResolver.sanitize("[accent]text[]\n\t\u0000icon");
        check(value.equals("[accent]text[]\n\ticon"), "text sanitization changed legal formatting");
    }

    private static void instructionalTextSelectionIsScopedAndFormatSafe(){
        check(RandomInstructionTextController.isInstructionText("lst.drawflush", "Flush queued Draw operations."),
            "logic instruction tooltip was not selected");
        check(RandomInstructionTextController.isInstructionText("hint.zoom", "Scroll to zoom."),
            "tutorial hint was not selected");
        check(!RandomInstructionTextController.isInstructionText("settings.graphics", "Graphics"),
            "normal UI text was selected");
        check(!RandomInstructionTextController.isInstructionText("hint.dynamic", "Wave {0}"),
            "formatted instruction text was selected");
        check(RandomInstructionTextController.hasFormatArguments("Value {0,number}"),
            "formatted argument was not detected");
    }

    private static void formatArgumentPatternIsAndroidSafe(){
        String pattern = RandomInstructionTextController.FORMAT_ARGUMENT.pattern();
        check(!hasAndroidUnsafeBraces(pattern),
            "format argument pattern uses braces Android ICU rejects: " + pattern);
        check(RandomInstructionTextController.hasFormatArguments("Value {0}"),
            "simple format argument was not detected");
        check(RandomInstructionTextController.hasFormatArguments("Value {0,number}"),
            "patterned format argument was not detected");
        check(!RandomInstructionTextController.hasFormatArguments("Value without arguments"),
            "plain text was treated as a format argument");
    }

    /**
     * Mirrors Android's ICU regex parser for braces: unlike desktop java.util.regex, ICU only accepts
     * an unescaped brace as part of a well-formed quantifier ({n}, {n,} or {n,m}); any other unescaped
     * '{' or '}' outside a character class is a PatternSyntaxException there.
     */
    private static boolean hasAndroidUnsafeBraces(String pattern){
        boolean inClass = false;
        for(int i = 0; i < pattern.length(); i++){
            char c = pattern.charAt(i);
            if(c == '\\'){
                i++;
            }else if(inClass){
                if(c == ']') inClass = false;
            }else if(c == '['){
                inClass = true;
            }else if(c == '{'){
                int j = i + 1;
                while(j < pattern.length() && Character.isDigit(pattern.charAt(j))) j++;
                if(j == i + 1) return true;
                if(j < pattern.length() && pattern.charAt(j) == ',') j++;
                while(j < pattern.length() && Character.isDigit(pattern.charAt(j))) j++;
                if(j >= pattern.length() || pattern.charAt(j) != '}') return true;
                i = j;
            }else if(c == '}'){
                return true;
            }
        }
        return false;
    }

    private static void textureFitIsAspectPreserving(){
        float scale = RandomTextureController.fitScale(64, 32, 1f, 32, 32, 1f);
        check(Math.abs(scale - 0.5f) < 0.0001f, "texture fit scale is not aspect preserving: " + scale);
    }

    private static void check(boolean value, String message){
        if(!value) throw new AssertionError(message);
    }
}
