package io.github.martinschneider.juvavumrest;

import jakarta.servlet.http.HttpServletRequest;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Longs;
import com.oath.halodb.HaloDB;
import com.oath.halodb.HaloDBException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;

@RestController
public class PositionController implements HandlerInterceptor {

  private static Logger LOG = LoggerFactory.getLogger(PositionController.class);

  private static final String[] GAMES = {"JUV", "DJUV", "CRAM", "DOM"};

  @Autowired private HaloDB db;

  @CrossOrigin(origins = "https://juvavum.5164.at")
  @GetMapping("/")
  public ResponseEntity<List<Long>> result(
      HttpServletRequest request,
      @RequestParam(value = "g", defaultValue = "1") int game,
      @RequestParam(value = "h") int h,
      @RequestParam(value = "w") int w,
      @RequestParam(value = "m") boolean misere,
      @RequestParam(value = "b") long b)
      throws HaloDBException {
    CacheControl cacheControl =
        CacheControl.maxAge(30, TimeUnit.MINUTES).noTransform().mustRevalidate();
    List<Long> ret = parseValues(db.get(buildKey(game, h, w, misere, b)));
    LOG.info(
        "{} {}x{}{} {} -> {} from {}",
        GAMES[game - 1],
        h,
        w,
        misere ? " (misere)" : "",
        b,
        ret,
        request.getRemoteAddr());
    return ResponseEntity.ok().cacheControl(cacheControl).body(ret);
  }

  private static byte[] buildKey(int game, int h, int w, boolean misere, long pos) {
    return Bytes.concat(
        new byte[] {(byte) game},
        new byte[] {(byte) h},
        new byte[] {(byte) w},
        new byte[] {(byte) (misere ? 1 : 0)},
        Longs.toByteArray(pos));
  }

  private static List<Long> parseValues(byte[] bytes) {
    if (bytes == null) {
      return Collections.emptyList();
    }
    List<Long> values = new ArrayList<>();
    int i = 0;
    while (i + 8 <= bytes.length) {
      values.add(Longs.fromByteArray(Arrays.copyOfRange(bytes, i, i + 8)));
      i += 8;
    }
    return values;
  }
}
