package org.folio.holdingsiq.deserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.List;
import org.folio.holdingsiq.model.AlternateName;
import org.folio.holdingsiq.model.PackageData;
import org.junit.jupiter.api.Test;

class AlternateNameListDeserializerTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  void deserialize_arrayOfObjects_returnsAlternateNames() throws Exception {
    var json = packageWith(
      "[{\"id\":1,\"altName\":\"Foo\"},{\"id\":2,\"altName\":\"Bar\"}]");

    List<AlternateName> result = readManagedAltNames(json);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(new AlternateName(1, "Foo"), result.get(0));
    assertEquals(new AlternateName(2, "Bar"), result.get(1));
  }

  @Test
  void deserialize_arrayOfObjects_missingId_setsIdToNull() throws Exception {
    var json = packageWith("[{\"altName\":\"OnlyName\"}]");

    List<AlternateName> result = readManagedAltNames(json);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNull(result.getFirst().id());
    assertEquals("OnlyName", result.getFirst().altName());
  }

  @Test
  void deserialize_arrayOfStrings_wrapsEachStringAsAltName() throws Exception {
    var json = packageWith("[\"Alpha\",\"Beta\",\"Gamma\"]");

    List<AlternateName> result = readManagedAltNames(json);

    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals(new AlternateName(null, "Alpha"), result.get(0));
    assertEquals(new AlternateName(null, "Beta"), result.get(1));
    assertEquals(new AlternateName(null, "Gamma"), result.get(2));
  }

  @Test
  void deserialize_emptyArray_returnsEmptyList() throws Exception {
    var json = packageWith("[]");

    List<AlternateName> result = readManagedAltNames(json);

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void deserialize_mixedObjectsAndStrings_handlesEachCorrectly() throws Exception {
    var json = packageWith("[{\"id\":1,\"altName\":\"ObjName\"},\"StrName\"]");

    List<AlternateName> result = readManagedAltNames(json);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(new AlternateName(1, "ObjName"), result.get(0));
    assertEquals(new AlternateName(null, "StrName"), result.get(1));
  }


  @Test
  void deserialize_notAnArray_throwsMismatchedInputException() {
    var json = packageWith("{\"id\":1,\"altName\":\"Foo\"}");

    assertThrows(MismatchedInputException.class, () -> readManagedAltNames(json));
  }

  @Test
  void deserialize_arrayContainingNumber_throwsMismatchedInputException() {
    var json = packageWith("[42]");

    assertThrows(MismatchedInputException.class, () -> readManagedAltNames(json));
  }

  private static String packageWith(String value) {
    return "{\"" + "managedAltNames" + "\":" + value + "}";
  }

  private static List<AlternateName> readManagedAltNames(String json) throws Exception {
    return MAPPER.readValue(json, PackageData.class).getManagedAltNames();
  }
}
