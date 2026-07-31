package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.jena.atlas.json.JsonObject;
import org.junit.jupiter.api.Test;

final class RewriteResultTest {
    @Test
    void returnedRecordArraysCannotMutateTheResult() {
        RewriteResult result = new RewriteResult("rewritten", "ASK {}");
        JsonObject rewrite = new JsonObject();
        rewrite.put("rule_id", "rule-1");
        result.addRewrite(rewrite);

        result.rewrites().add(new JsonObject());

        assertEquals(1, result.rewrites().size());
        assertEquals(1, result.toJson("query-1")
                .get("rewrites").getAsArray().size());
    }

    @Test
    void appendsCompleteRuleStepThroughOneOperation() {
        RewriteResult complete = new RewriteResult("unchanged", "ASK {}");
        RewriteResult step = new RewriteResult("rewritten", null);
        step.addRewrite(new JsonObject());
        step.addWarning(new JsonObject());
        step.addError(new JsonObject());

        complete.append(step);

        assertEquals(1, complete.rewrites().size());
        assertEquals(1, complete.warnings().size());
        assertEquals(1, complete.errors().size());
    }
}
