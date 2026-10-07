package lab.auth;

import java.io.IOException;
import java.nio.file.Path;

/** Authorization policy loaded from external files at server startup. */
interface AccessPolicy {
    boolean allows(String username, String operation);

    String description();

    static AccessPolicy load(String mode, Path policyFile) throws IOException {
        if ("acl".equalsIgnoreCase(mode)) {
            return AclPolicy.load(policyFile);
        }
        if ("rbac".equalsIgnoreCase(mode)) {
            return RbacPolicy.load(policyFile);
        }
        throw new IOException("Policy mode must be 'acl' or 'rbac'.");
    }
}
