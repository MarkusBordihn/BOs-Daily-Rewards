package de.markusbordihn.dailyrewards.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.markusbordihn.dailyrewards.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Config {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private static final Logger log = LogManager.getLogger(Constants.LOG_NAME);

  protected transient String root =
      "config%s%s%s".formatted(File.separator, Constants.MOD_ID, File.separator);
  protected transient String extension = ".json";

  public abstract String getPath();

  protected abstract void reset();

  private File getConfigFile() {
    return new File(this.root + this.getPath() + this.extension);
  }

  public void write() throws IOException {
    File dir = new File(this.root);
    if (!dir.exists() && !dir.mkdirs()) return;
    if (!this.getConfigFile().exists() && !this.getConfigFile().createNewFile()) return;
    FileWriter writer = new FileWriter(this.getConfigFile());
    GSON.toJson(this, writer);
    writer.flush();
    writer.close();
  }

  public <T extends Config> T read() {
    try {
      return GSON.fromJson(new FileReader(this.getConfigFile()), (Type) this.getClass());
    } catch (FileNotFoundException ignored) {
      this.reset();

      try {
        this.write();
      } catch (IOException e) {
        log.error("Failed to write default config file: {}", this.getConfigFile(), e);
      }
    }

    return (T) this;
  }
}
