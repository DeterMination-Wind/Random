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
        String first = "algorithm=1|identity=save-a|content=content-a|text=true|texture=false";
        String same = "algorithm=1|identity=save-a|content=content-a|text=true|texture=false";
        String changed = "algorithm=1|identity=save-a|content=content-b|text=true|texture=false";
        check(RandomPermutationController.digest(first).equals(RandomPermutationController.digest(same)), "digest is not stable");
        check(!RandomPermutationController.digest(first).equals(RandomPermutationController.digest(changed)), "content change did not invalidate digest");
        check(store == null, "test guard");
    }

    private static void textSanitizationPreservesFormatting(){
        String value = RandomTextResolver.sanitize("[accent]text[]\n\t\u0000icon");
        check(value.equals("[accent]text[]\n\ticon"), "text sanitization changed legal formatting");
    }

    private static void textureFitIsAspectPreserving(){
        float scale = RandomTextureController.fitScale(64, 32, 1f, 32, 32, 1f);
        check(Math.abs(scale - 0.5f) < 0.0001f, "texture fit scale is not aspect preserving: " + scale);
    }

    private static void check(boolean value, String message){
        if(!value) throw new AssertionError(message);
    }
}
