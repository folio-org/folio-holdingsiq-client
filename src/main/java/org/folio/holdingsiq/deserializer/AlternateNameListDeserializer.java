package org.folio.holdingsiq.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.folio.holdingsiq.model.AlternateName;

/**
 * Custom Jackson deserializer for a {@code List<AlternateName>} that handles two JSON representations:
 *
 * <ul>
 *   <li><b>Array of objects</b> – each element is deserialized normally into an {@link AlternateName}.</li>
 *   <li><b>Array of strings</b> – each string value is wrapped in a new {@link AlternateName}
 *       with {@code id = null} and {@code altName} set to the string value.</li>
 * </ul>
 *
 * <p>Example JSON inputs:
 * <pre>{@code
 * // Array of objects (normal deserialization):
 * [{"id": "1", "altName": "Foo"}, {"id": "2", "altName": "Bar"}]
 *
 * // Array of strings (wrapped into AlternateName):
 * ["Foo", "Bar"]
 * }</pre>
 */
public class AlternateNameListDeserializer extends StdDeserializer<List<AlternateName>> {

  /**
   * Default constructor required by Jackson.
   */
  public AlternateNameListDeserializer() {
    super(List.class);
  }

  /**
   * Deserializes a JSON array into a list of {@link AlternateName} objects.
   * Each element may be either a JSON object or a plain string.
   *
   * @param p   the {@link JsonParser} positioned at the start of the array
   * @param ctx the {@link DeserializationContext} used for delegated deserialization
   * @return a list of {@link AlternateName} instances, never {@code null} on a valid array token
   * @throws IOException if the token is not an array, or if an element has an unexpected type
   */
  @Override
  public List<AlternateName> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
    if (!p.isExpectedStartArrayToken()) {
      ctx.reportWrongTokenException(List.class, JsonToken.START_ARRAY, "Expected array");
      return Collections.emptyList();
    }

    List<AlternateName> result = new ArrayList<>();
    while (p.nextToken() != JsonToken.END_ARRAY) {
      if (p.currentToken() == JsonToken.START_OBJECT) {
        result.add(ctx.readValue(p, AlternateName.class));
      } else if (p.currentToken() == JsonToken.VALUE_STRING) {
        result.add(new AlternateName(null, p.getText()));
      } else {
        ctx.reportWrongTokenException(AlternateName.class, JsonToken.START_OBJECT,
          "Expected object or string in AlternateName array");
      }
    }
    return result;
  }
}
