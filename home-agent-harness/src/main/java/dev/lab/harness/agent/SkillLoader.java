package dev.lab.harness.agent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;

public final class SkillLoader {

	private SkillLoader() {}

	public static String load(String classpathLocation) {
		try (var input = new ClassPathResource(classpathLocation).getInputStream()) {
			return new String(input.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException exception) {
			throw new IllegalStateException("Could not read skill " + classpathLocation, exception);
		}
	}
}
