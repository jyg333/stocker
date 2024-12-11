import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AuthState {
    accessToken: string | null;
    member : string | null;
    auth_level: string | null;

}

const initialState: AuthState = {
    accessToken: null,
    member : null,
    auth_level:null,

};

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        setTokens: (state, action: PayloadAction<{ accessToken: string ,auth_level: string }>) => {
            state.accessToken = action.payload.accessToken;
            state.auth_level = action.payload.auth_level;
        },
        clearTokens: (state) => {
            state.accessToken = null;
            state.auth_level = null;
        },
        setMember: (state, action: PayloadAction<string>)  => {
            state.member = action.payload
        }
    },
});

export const { setTokens, clearTokens, setMember } = authSlice.actions;

export default authSlice.reducer;
