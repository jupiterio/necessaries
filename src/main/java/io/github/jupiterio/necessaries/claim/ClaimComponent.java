package io.github.jupiterio.necessaries.claim;

import nerdhub.cardinal.components.api.component.Component;

public interface ClaimComponent extends Component {
    int getId();
    void setId(int id);
    Claim getClaimData();
}
