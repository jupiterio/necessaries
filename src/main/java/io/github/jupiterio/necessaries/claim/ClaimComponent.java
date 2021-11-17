package io.github.jupiterio.necessaries.claim;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface ClaimComponent extends Component {
    int getId();
    void setId(int id);
    Claim getClaimData();
}
