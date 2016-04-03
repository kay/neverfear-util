package org.neverfear.util.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.neverfear.util.Environment;

public class Main {
	public static void main(String[] args) throws InterruptedException, IOException {
		if (args.length == 0) {
			File directory = new File("target/stream");
			directory.mkdirs();

			File stdout = new File(directory, "stdout");
			stdout.delete();

			System.out.println("Executing");
			Process process = new ProcessBuilder("java", "-cp", Environment.CLASSPATH, Main.class.getName(),
					"Message here").redirectOutput(stdout).start();

//			while (process.isAlive()) {
//				try (BufferedReader reader = new BufferedReader(new FileReader(stdout))) {
//					String line = null;
//					while(true) {
//						line = reader.readLine();
//						if (line != null) {
//							System.out.println(line);
//						} else if (!process.isAlive()) {
//							break;
//						}
//					} 
//				} catch (FileNotFoundException e) {
//					System.out.println("Not yet created");
//					TimeUnit.MILLISECONDS.sleep(10);
//					continue;
//				}
//			}
			InputStream inputStream = ProcessUtil.tryOpenStreamOfProcess(process, stdout, 10, TimeUnit.SECONDS);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
				String line = null;
				while(true) {
					line = reader.readLine();
					if (line != null) {
						System.out.println(line);
					} else if (!process.isAlive()) {
						break;
					}
				} 
			}

			int code = process.waitFor();
			System.out.println("Exit code=" + code);

		} else {
			for (int i = 0; i < 10; i++) {
				System.out.println(new Date() + " " + Arrays.toString(args));
				TimeUnit.SECONDS.sleep(1);
			}
		}
	}
}
