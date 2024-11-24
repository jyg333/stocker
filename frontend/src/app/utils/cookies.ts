import {
    setCookie as set_cookie,
    getCookie as get_cookie,
    deleteCookie,
} from "cookies-next";

export const setCookie = (name: string, value: string) => {
    set_cookie(name, value);
    return;
};

export const getCookie = (name: string) => {
    return get_cookie(name);
};

export const removeCookie = (name: string) => {
    deleteCookie(name);
};
