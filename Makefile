GRAALVM_IMAGE = ghcr.io/graalvm/native-image-community:25

.PHONY: build-HelloFunction

build-HelloFunction:
	docker run --rm --platform linux/arm64 \
		--entrypoint /bin/sh \
		-v "$(CURDIR):/project" \
		-w /project \
		$(GRAALVM_IMAGE) \
		-c "./gradlew nativeCompile"
	cp build/native/nativeCompile/bootstrap $(ARTIFACTS_DIR)/
